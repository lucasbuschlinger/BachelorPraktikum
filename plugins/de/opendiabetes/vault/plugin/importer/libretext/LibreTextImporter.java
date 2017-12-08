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
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.AbstractImporter;
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
    public static class LibreTextImporterImplementation extends AbstractImporter {

        /**
         * The path to the import file.
         */
        private String importFilePath;

        /**
         * German header for libre csv data.
         */
        private static final String[][] LIBRE_CSV_HEADER = {{"Uhrzeit", "Art des Eintrags",
                "Historische Glukose (mg/dL)", "Gescannte Glukose (mg/dL)", "Teststreifen-Blutzucker (mg/dL)"}};
        /**
         * Types used in LibreText data.
         */
        private static final String[] LIBRE_TYPE = {"ScanGlucose", "HistoricGlucose", "BloodGlucose", "TimeChanged"};
        /**
         * Integers used in LibreText data.
         */
        private static final int[] LIBRE_TYPE_INTEGER = {1, 0, 2, 6};
        /**
         * Time format used in LibreText data.
         */
        private static final String TIME_FORMAT_LIBRE_DE = "yyyy.MM.dd HH:mm";

        /**
         * {@inheritDoc}
         */
        @Override
        public String getImportFilePath() {
            return importFilePath;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setImportFilePath(final String filePath) {
            this.importFilePath = filePath;
        }

        /**
         * Unimplemented method for data importation (not required by LibreText importer).
         * @return Nothing, throws {@link UnsupportedOperationException}
         * @throws UnsupportedOperationException This method is not implemented, as it is not needed.
         */
        @Override
        public boolean importData() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Method importData is not supported by the LibreText importer!");
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
        private static RawEntry parseEntry(final CsvReader reader) throws IOException, ParseException {

            String[] validHeader = LIBRE_CSV_HEADER[0];
            int type = Integer.parseInt(reader.get(validHeader[1]));

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

            return null;

        }
    }
}
