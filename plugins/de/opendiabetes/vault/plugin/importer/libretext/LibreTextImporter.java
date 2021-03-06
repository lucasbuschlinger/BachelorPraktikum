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
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.fileimporter.CSVImporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Wrapper class for the LibreTextImporter plugin.
 *
 * @author Lucas Buschlinger
 * @author Magnus Gärtner
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
    public static final class LibreTextImporterImplementation extends CSVImporter {

        /**
         * Constructor for a CSV validator.
         */
        public LibreTextImporterImplementation() {
            super(new LibreTextCSVValidator());
        }


        /**
         * Parser for libre text based entries.
         *
         * @param reader The CSV Reader.
         * @return Parsed entry.
         * @throws Exception Thrown by the CSV Reader.
         */
        @Override
        public List<VaultEntry> parseEntry(final CsvReader reader) throws Exception {
            List<VaultEntry> retVal = new ArrayList<>();
            LibreTextCSVValidator parseValidator = (LibreTextCSVValidator) getValidator();

            LibreTextCSVValidator.TYPE type = parseValidator.getType(reader);
            if (type == null) {
                return null;
            }

            Date timestamp;
            try {
                timestamp = parseValidator.getTimestamp(reader);
            } catch (ParseException exception) {
                return null;
            }

            VaultEntry tempEntry;
            double value;

            switch (type) {
                case HISTORIC_GLUCOSE:
                    value = parseValidator.getHistoricGlucose(reader);
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM, timestamp, value);
                    tempEntry.addAnnotation(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.CGM_VENDOR_LIBRE));
                    break;
                case SCAN_GLUCOSE:
                    value = parseValidator.getScanGlucose(reader);
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM_ALERT, timestamp, value);
                    break;
                case BLOOD_GLUCOSE:
                    value = parseValidator.getBloodGlucose(reader);
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_BG, timestamp, value);
                    break;
                case TIME_CHANGED:
                    timestamp = parseValidator.getTimeSync(reader);
                    tempEntry = new VaultEntry(VaultEntryType.CGM_TIME_SYNC, timestamp, VaultEntry.VALUE_UNUSED);
                    break;
                case CARBOHYDRATES:
                    value = parseValidator.getCarbohydrates(reader);
                    tempEntry = new VaultEntry(VaultEntryType.MEAL_MANUAL, timestamp, value);
                    break;
                //case BOLUS_NORMAL: TODO with file that contains data about this
                //   value = parseValidator.getBolusNormal(reader);
                //    tempEntry = new VaultEntry(VaultEntryType.BOLUS_NORMAL, timestamp, value);
                //    break;
                 default:
                    return null;
            }
            retVal.add(tempEntry);
            return retVal;

        }

    }
}
