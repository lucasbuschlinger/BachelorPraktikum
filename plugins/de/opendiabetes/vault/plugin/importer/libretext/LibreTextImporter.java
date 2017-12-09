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
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.validator.LibreTextCSVValidator;
import de.opendiabetes.vault.plugin.util.TimestampUtils;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static de.opendiabetes.vault.plugin.importer.libretext.LibreTextImporter.LibreTextImporterImplementation.LIBRE_TYPE.*;

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

        protected enum LIBRE_TYPE {
            SCAN_GLUCOSE (1, "Gescannte Glukose (mg/dL)"),
            HISTORIC_GLUCOSE (0, "Historische Glukose (mg/dL)"),
            BLOOD_GLUCOSE (2, "Teststreifen-Blutzucker (mg/dL)"),
            TIME_CHANGED (6, "Uhrzeit");
            final int index;
            final String header;
            LIBRE_TYPE(int index, String header) {
                this.index = index;
                this.header = header;
            }
            final int getIndex(){
                return index;
            }
            final String getHeader(){
                return header;
            }

            static LIBRE_TYPE getFromType(int type){
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
                        LOG.log(Level.SEVERE, "Error while type checking!");
                        throw new IllegalArgumentException("No such type for LibreText data.");
                }
            }
        }

        /**
         * Constructor for a CSV validator.
         */
        public LibreTextImporterImplementation() {
            super(new LibreTextCSVValidator(), '\t');
        }

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
         * Parser for libre text based entries.
         *
         * @param reader The CSV Reader.
         * @return Parsed entry.
         * @throws IOException Thrown by the CSV Reader.
         * @throws ParseException Thrown by the CSV Reader.
         */
        @Override
        public List<VaultEntry> parseEntry(final CsvReader reader) throws IOException, ParseException {

            int type = Integer.parseInt(reader.get("Art des Eintrags"));

            List<VaultEntry> retVal = new ArrayList<>();

            VaultEntry tempEntry;
            double value;
            Date timestamp = TimestampUtils.createCleanTimestamp(reader.get(TIME_CHANGED.getHeader()), TIME_FORMAT_LIBRE_DE);

            switch (LIBRE_TYPE.getFromType(type)) {
                case HISTORIC_GLUCOSE:
                    value = Double.parseDouble(reader.get(HISTORIC_GLUCOSE.getHeader()));
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM, timestamp, value);
                    tempEntry.addAnnotation(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.CGM_VENDOR_LIBRE));
                    break;
                case SCAN_GLUCOSE:
                    value = Double.parseDouble(reader.get(SCAN_GLUCOSE.getHeader()));
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_CGM_ALERT, timestamp, value);
                    break;
                case BLOOD_GLUCOSE:
                    value = Double.parseDouble(reader.get(BLOOD_GLUCOSE.getHeader()));
                    tempEntry = new VaultEntry(VaultEntryType.GLUCOSE_BG, timestamp, value);
                    break;
                 default:
                    return null;
            }
            retVal.add(tempEntry);
            return retVal;

        }

        @Override
        protected void preprocessingIfNeeded(final String filePath) {}
    }
}
