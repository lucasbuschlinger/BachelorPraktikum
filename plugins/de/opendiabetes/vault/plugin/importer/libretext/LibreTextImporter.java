/*
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
package de.opendiabetes.vault.plugin.importer.libretext;

import com.csvreader.CsvReader;
import com.sun.javaws.exceptions.InvalidArgumentException;
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.AbstractImporter;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.validator.CSVValidator;
import de.opendiabetes.vault.plugin.util.TimestampUtils;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static de.opendiabetes.vault.plugin.importer.libretext.LibreTextImporter.LibreTextImporterImplementation.LIBRE_TYPE.HISTORIC_GLUCOSE;
import static de.opendiabetes.vault.plugin.importer.libretext.LibreTextImporter.LibreTextImporterImplementation.LIBRE_TYPE.SCAN_GLUCOSE;
import static de.opendiabetes.vault.plugin.importer.libretext.LibreTextImporter.LibreTextImporterImplementation.LIBRE_TYPE.TIME_CHANGED;

/**
 * Wrapper class for the LibreTextImporter plugin.
 */
public class LibreTextImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     * @param wrapper The PluginWrapper.
     */
    public LibreTextImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the LibreText importer plugin.
     */
    @Extension
    public static class LibreTextImporterImplementation extends CSVImporter {

        /**
         * Time format used in LibreText data.
         */
        private static final String TIME_FORMAT_LIBRE_DE = "yyyy.MM.dd HH:mm";

        private enum LIBRE_TYPE {
            SCAN_GLUCOSE (1, "ScanGlucose", "Gescannte Glukose (mg/dL)"),
            HISTORIC_GLUCOSE (0, "HistoricGlucose", "Historische Glukose (mg/dL)"),
            BLOOD_GLUCOSE (2, "BloodGlucose", "Teststreifen-Blutzucker (mg/dL)"),
            TIME_CHANGED (6, "TimeChanged", "Uhrzeit");
            final int index;
            final String name;
            final String header;
            LIBRE_TYPE(int index, String name, String header) {
                this.index = index;
                this.name = name;
                this.header = header;
            }
            final int getIndex(){
                return index;
            }
            final String getName(){
                return name;
            }
            final String getHeader(){
                return header;
            }
            final static LIBRE_TYPE getFromType(int type){
                switch (type) {
                    case 1:
                        return SCAN_GLUCOSE;
                    case 0:
                        return HISTORIC_GLUCOSE;
                    case 2:
                        return BLOOD_GLUCOSE;
                    case 6:
                        return TIME_CHANGED;
                    default:
                        throw new IllegalArgumentException("No such type for LibreText data.");
                }
            }
        }

        /**
         * Constructor for a CSV validator.
         *
         * @param csvValidator The validator to use.
         * @param csvDelimiter The delimiter used in the CSV file.
         */
        public LibreTextImporterImplementation() {
            super(new csvLibreValidator(), '\t');
        }

        /**
         * Unimplemented method for data importation (not required by LibreText importer).
         * @return Nothing, throws {@link UnsupportedOperationException}
         * @throws UnsupportedOperationException This method is not implemented, as it is not needed.
         */
       /*
        public boolean importData() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Method importData is not supported by the LibreText importer!");
        }*/

        /**
         * Method to load configuration file for the LibreTextImporter plugin.
         *
         * @param filePath Path to the configuration file.
         * @return True when configuration can be loaded, false otherwise.
         */
        @Override
        public boolean loadConfiguration(final String filePath) {
            LOG.log(Level.WARNING, "LibreTextImporter does not support configuration.");
            return false;
        }

        /**
         * Parser for data form LibreText files.
         *
         * @param filePath Path to the file to be imported.
         * @return List of RawEntry containing the parsed data.
         * @throws FileNotFoundException If the file specified in filePath does not lead to a valid file.
         */
        //TODO: RawEntry vs RawDataEntry? Ask Jens... & What's up with all these TODOs?
        public static List<RawEntry> parseData(final String filePath) throws FileNotFoundException {
            List<RawEntry> libreEntries = new ArrayList<>();
            // read file
            CsvReader creader = new CsvReader(filePath, '\t', Charset.forName("UTF-8"));

            // TODO add stuff
            // Teststreifen-Blutzucker (mg/dL)
            // Keton (mmol/L)
            try {
                // validate header
                // TODO implement a header-erkenner
                for (int i = 0; i < 2; i++) {
                    creader.readHeaders();
                    //TODO compute meta data
                }
                //TODO check header data
                //TODO: Carelink?
//            if (!CsvValidator.validateCarelinkHeader(creader)) {
//                Logger.getLogger(CarelinkCsvImporter.class.getName()).
//                        log(Level.SEVERE,
//                                "Stop parser because of unvalid header:\n"
//                                + Arrays.toString(Constants.CARELINK_CSV_HEADER[0])
//                                + "\n{0}", creader.getRawRecord());
//                return null;
//            }

                // read entries
                while (creader.readRecord()) {
                    // Todo categorize entry
                    RawEntry entry = parseEntry(creader);
                    if (entry != null) {
                        libreEntries.add(entry);
                        LOG.log(Level.INFO, "Got Entry: {0}", entry.toString());
                    } else {
                        LOG.log(Level.FINE, "Drop Entry: {0}", creader.getRawRecord());
                    }

                }

            } catch (IOException | ParseException ex) {
                LOG.log(Level.SEVERE, "Error while parsing LibreText CSV", ex);
            } finally {
                creader.close();
            }
            return libreEntries;
        }

        /**
         * Parser for libre text based entries.
         *
         * @param reader The CSV Reader.
         * @return //TODO: Always null?.
         * @throws IOException Thrown by the CSV Reader.
         * @throws ParseException Thrown by the CSV Reader.
         */
        @Override
        public List<VaultEntry> parseEntry(final CsvReader reader) throws IOException, ParseException {

            String[] validHeader = LIBRE_CSV_HEADER[0];
            int type = Integer.parseInt(reader.get("Art des Eintrags"));

            if (type == LIBRE_TYPE_INTEGER[1]) { // HistoricGlucose
                Date timestamp = TimestampUtils.createCleanTimestamp(reader.get(validHeader[0]), TIME_FORMAT_LIBRE_DE);
                double value = Double.parseDouble(reader.get(validHeader[2]));

                VaultEntry tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM, timestamp, value);
                tempEntry.addAnnotation(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.CGM_VENDOR_LIBRE));
                //TODO: uncomment once DB interface has been implemented.
                //VaultDao.getInstance().putEntry(tempEntry);

            } else if (type == LIBRE_TYPE_INTEGER[0]) { // ScanGlucose
                Date timestamp = TimestampUtils.createCleanTimestamp(reader.get(validHeader[0]), TIME_FORMAT_LIBRE_DE);
                double value = Double.parseDouble(reader.get(validHeader[3]));

                //TODO: uncomment once DB interface has been implemented.
                //VaultDao.getInstance().putEntry(new VaultEntry(VaultEntryType.GLUCOSE_CGM_ALERT,timestamp, value));
            } else if (type == LIBRE_TYPE_INTEGER[2]) { // BloodGlucose
                Date timestamp = TimestampUtils.createCleanTimestamp(reader.get(validHeader[0]), TIME_FORMAT_LIBRE_DE);
                double value = Double.parseDouble(reader.get(validHeader[4]));

                //TODO: uncomment once DB interface has been implemented.
                //VaultDao.getInstance().putEntry(new VaultEntry(VaultEntryType.GLUCOSE_BG, timestamp, value));
            } else {
                LOG.log(Level.SEVERE, "Error while type checking!");
                return null;
            }

            VaultEntry tempEntry;
            Date timestamp = TimestampUtils.createCleanTimestamp(reader.get(TIME_CHANGED.getHeader()), TIME_FORMAT_LIBRE_DE);
            switch (LIBRE_TYPE.getFromType(type)) {
                case HISTORIC_GLUCOSE:
                    double value = Double.parseDouble(reader.get(HISTORIC_GLUCOSE.getHeader()));

                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM, timestamp, value);
                    tempEntry.addAnnotation(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.CGM_VENDOR_LIBRE));
                    break;
                case SCAN_GLUCOSE:
                    double value = Double.parseDouble(reader.get(SCAN_GLUCOSE.getHeader()));
                    tempEntry = (new VaultEntry(VaultEntryType.GLUCOSE_CGM_ALERT,timestamp, value);
                    break;
            }
            return tempEntry;

        }

        @Override
        protected void preprocessingIfNeeded(final String filePath) {}
    }
}
