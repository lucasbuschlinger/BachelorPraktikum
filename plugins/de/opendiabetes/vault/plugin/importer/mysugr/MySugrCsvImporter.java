/**
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer.mysugr;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.validator.MySugrCSVValidator;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Wrapper class for MySugrCsvImporter plugin.
 */
public class MySugrCsvImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public MySugrCsvImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the MySugrCsvImporter plugin.
     */
    @Extension
    public static class MySugrCsvImporterImplementation extends CSVImporter {

        /**
         * The maximum number of lines to scan for a header.
         */
        private static final int MAX_HEADER_LINES_SCAN = 15;

        /**
         * Constructor.
         *
         * @param delimiter The delimiter that should be used.
         */
        public MySugrCsvImporterImplementation(final char delimiter) {
            super(new MySugrCSVValidator(), delimiter);
        }

        /**
         * Extracts a VaultEntry with the data type double.
         *
         * @param timestamp The timestamp at which the entry occurred.
         * @param type The type of the entry.
         * @param rawValue The raw value of the entry.
         * @param fullEntry The full entry.
         * @return VaultEntry The extracted double entry as a VaultEntry.
         */
        private static VaultEntry extractDoubleEntry(final Date timestamp, final VaultEntryType type,
                                                     final String rawValue, final String[] fullEntry) {
            if (rawValue != null && !rawValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(rawValue.replace(",", "."));
                    return new VaultEntry(type, timestamp, value);
                } catch (NumberFormatException ex) {
                    LOG.log(Level.WARNING, "{0} -- Record: {1}",
                            new Object[]{ex.getMessage(), Arrays.toString(fullEntry)});
                }
            }
            return null;
        }

        /**
         * Method to parse entries in a file.
         *
         * @param csvReader Reader for CSV files.
         * @return List of VaultEntry holding the imported data.
         * @throws Exception If a entry could not be parsed.
         */
        @Override
        protected List<VaultEntry> parseEntry(final CsvReader csvReader)
                throws Exception {
            List<VaultEntry> retVal = new ArrayList<>();
            MySugrCSVValidator parseValidator = (MySugrCSVValidator) this.getValidator();

            Date timestamp;
            try {
                timestamp = parseValidator.getManualTimestamp(csvReader);
            } catch (ParseException ex) {
                //should not happen
                timestamp = null;
            }
            if (timestamp == null) {
                return null;
            }

            String rawValue;
            VaultEntry tmpEntry;

            // Glucose_BG
            rawValue = parseValidator.getBGMeasurement(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.GLUCOSE_BG_MANUAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    retVal.add(tmpEntry);
                }
            }

            //MEAL_MANUAL
            rawValue = parseValidator.getMealCarbs(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.MEAL_MANUAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    String tmpFoodType = parseValidator.getFoodType(csvReader);
                    if (!tmpFoodType.isEmpty()) {
                        VaultEntryAnnotation foodType = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.MEAL_Information);
                        foodType.setValue(tmpFoodType);
                        tmpEntry.addAnnotation(foodType);
                    }
                    retVal.add(tmpEntry);
                }
            }

            //BASAL_MANUAL
            rawValue = parseValidator.getBasalUnits(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.BASAL_MANUAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    retVal.add(tmpEntry);
                }
            }

            //BOLUS_NORMAL
            rawValue = parseValidator.getBolusNormal(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.BOLUS_NORMAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    retVal.add(tmpEntry);
                    //if bolus entry is present we add blood glucose value as GLUCOSE_BOLUS_CALCULATION too
                    rawValue = parseValidator.getBGMeasurement(csvReader);
                    if (!rawValue.isEmpty()) {
                        tmpEntry = extractDoubleEntry(timestamp,
                                VaultEntryType.GLUCOSE_BOLUS_CALCULATION, rawValue,
                                csvReader.getValues());
                        if (tmpEntry != null) {
                            String tmpInsulinMeal = parseValidator.getInsulinMeal(csvReader);
                            if (!tmpInsulinMeal.isEmpty()) {
                                VaultEntryAnnotation insulinMeal = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.BOLUS_MEAL);
                                insulinMeal.setValue(tmpInsulinMeal);
                                tmpEntry.addAnnotation(insulinMeal);
                            }
                            String tmpInsulinCorrection = parseValidator.getInsulinCorrection(csvReader);
                            if (!tmpInsulinCorrection.isEmpty()) {
                                VaultEntryAnnotation insulinCorrection =
                                        new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.BOLUS_CORRECTION);
                                insulinCorrection.setValue(tmpInsulinCorrection);
                                tmpEntry.addAnnotation(insulinCorrection);
                            }
                            retVal.add(tmpEntry);
                        }
                    }
                }
            }

            //EXERCISE_MANUAL
            rawValue = parseValidator.getActivity(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.EXERCISE_MANUAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    String intensity = parseValidator.getActivityIntensity(csvReader);
                    if (!intensity.isEmpty()) { //check the actual labels when they arrive and change it according to the CSV
                        VaultEntryAnnotation annotation = null;
                        switch (intensity) {
                            case "Cosy":
                                annotation = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_cosy);
                                break;
                            case "Ordinary":
                                annotation = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_ordinary);
                                break;
                            case "Demanding":
                                annotation = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_demanding);
                                break;
                        }
                        tmpEntry.addAnnotation(annotation);
                    }
                    retVal.add(tmpEntry);
                }
            }

            //KETONES_MANUAL
            rawValue = parseValidator.getKetones(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.KETONES_MANUAL, rawValue,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    retVal.add(tmpEntry);
                }
            }

            //BLOOD_PRESSURE
            rawValue = parseValidator.getBloodPressure(csvReader);
            if (!rawValue.isEmpty()) {
                String[] bloodPressureValues = rawValue.split("/");
                String bloodPressureDiastolic = bloodPressureValues[0].trim();
                String bloodPressureSystolic = bloodPressureValues[1].trim();
                tmpEntry = extractDoubleEntry(timestamp,
                        VaultEntryType.BLOOD_PRESSURE, bloodPressureDiastolic,
                        csvReader.getValues());
                if (tmpEntry != null) {
                    VaultEntryAnnotation annotationHigh = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.BLOOD_PRESSURE_Systolic);
                    annotationHigh.setValue(bloodPressureSystolic);
                    tmpEntry.addAnnotation(annotationHigh);
                    VaultEntryAnnotation annotationLow = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.BLOOD_PRESSURE_Diastolic);
                    annotationLow.setValue(bloodPressureDiastolic);
                    tmpEntry.addAnnotation(annotationLow);
                    tmpEntry.setValue2(Double.parseDouble(bloodPressureSystolic));
                    retVal.add(tmpEntry);
                }
            }

            //MEAL_DESCRIPTION
            rawValue = parseValidator.getMealDescriptions(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = new VaultEntry(VaultEntryType.MEAL_DESCRIPTION, timestamp,
                        VaultEntry.VALUE_UNUSED);
                if (tmpEntry != null) {
                    VaultEntryAnnotation mealDescription = new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.MEAL_Information);
                    mealDescription.setValue(rawValue);
                    tmpEntry.addAnnotation(mealDescription);
                }
            }

            //TAG
            rawValue = parseValidator.getTags(csvReader);
            if (!rawValue.isEmpty()) {
                tmpEntry = new VaultEntry(VaultEntryType.Tag, timestamp, VaultEntry.VALUE_UNUSED);
                String[] tmpTags = rawValue.split(",");
                ArrayList<VaultEntryAnnotation> tags = new ArrayList();
                for (int i = 0; i < tmpTags.length; i++) {
                    tags.add(new VaultEntryAnnotation(parseValidator.getTagAnnotation(tmpTags[i])));
                    if (tags.get(i).getType() == VaultEntryAnnotation.TYPE.TAG_Unknown) {
                        tags.get(i).setValue(tmpTags[i]);
                    }
                }
                String note = parseValidator.getNotes(csvReader);
                if (!note.isEmpty()) {
                    tags.add(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.TAG_Note));
                    tags.get(tmpTags.length).setValue(note);
                }
                tmpEntry.setAnnotation(tags);
                retVal.add(tmpEntry);
            }
            return retVal;
        }

        /**
         * This method finds a valid header in the CSV file if it exists.
         *
         * @param filePath The file path of the file to pre process.
         */
        @Override
        protected void preprocessingIfNeeded(final String filePath) {
            // test for delimiter
            CsvReader creader = null;
            MySugrCSVValidator validator = (MySugrCSVValidator) getValidator();
            try {
                // test for , delimiter
                creader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
                for (int i = 0; i < MAX_HEADER_LINES_SCAN; i++) { // just scan the first 15 lines for a valid header
                    if (creader.readHeaders()) {
                        if (validator.validateHeader(creader.getHeaders())) {
                            // found valid header --> finish
                            setDelimiter(',');
                            creader.close();
                            LOG.log(Level.FINE, "Use ',' as delimiter for MySugr CSV: {0}", filePath);
                            return;
                        }
                    }
                }
                // if you end up here there was no valid header within the range
                // try the other delimiter in normal operation
                setDelimiter(';');
                LOG.log(Level.FINE, "Use ';' as delimiter for MySugr CSV: {0}", filePath);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Error while parsing MySugr CSV in delimiter check: " + filePath, ex);
            } finally {
                if (creader != null) {
                    creader.close();
                }
            }
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            LOG.log(Level.WARNING, "MySugrCsvImporter does not support configuration");
            return false;
        }
    }
}
