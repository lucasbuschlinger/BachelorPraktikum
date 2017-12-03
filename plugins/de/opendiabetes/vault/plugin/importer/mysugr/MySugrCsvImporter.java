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
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.validator.MySugrCSVValidator;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class for MySugrCsvImporter plugin
 */
public class MySugrCsvImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     * @param wrapper The PluginWrapper.
     */
    public MySugrCsvImporter(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the MySugrCsvImporter plugin.
     */
    @Extension
    public static class MySugrCsvImporterImplementation extends CSVImporter {

        /**
         * Constructor.
         */
        public MySugrCsvImporterImplementation(char delimiter) {
            super(new MySugrCSVValidator(), delimiter);
        }

        /**
         * Method to parse entries in a file.
         *
         * @param csvReader Reader for CSV files.
         * @return List of VaultEntry holding the imported data.
         * @throws Exception If a entry could not be parsed.
         */
        @Override
        protected List<VaultEntry> parseEntry(CsvReader csvReader) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * This method finds a valid header in the CSV file if it exists
         * @param filePath The file path of the file to pre process
         */
        @Override
        protected void preprocessingIfNeeded(String filePath) {
            // test for delimiter
            CsvReader creader = null;
            MySugrCSVValidator validator = (MySugrCSVValidator) getValidator();
            try {
                // test for , delimiter
                creader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
                for (int i = 0; i < 15; i++) { // just scan the first 15 lines for a valid header
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
         * Method to load configuration file for the MySugrCsvImporter plugin.
         * @param path Path to the configuration file.
         * @return True when configuration can be loaded, false otherwise.
         */
        @Override
        public boolean loadConfiguration(String path) {
            LOG.log(Level.WARNING, "MySugrCsvImporter does not support configuration");
            return false;
        }
    }
}
