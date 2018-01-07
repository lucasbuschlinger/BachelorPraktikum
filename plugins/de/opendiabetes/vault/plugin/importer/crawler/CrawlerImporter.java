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
package de.opendiabetes.vault.plugin.importer.crawler;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Wrapper class for the CrawlerImporter plugin.
 *
 * @author Magnus Gärtner
 */
public class CrawlerImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public CrawlerImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the CrawlerImporter plugin.
     */
    @Extension
    public static class CrawlerImporterImplementation implements Importer {

        /**
         * Constructor.
         *
         * @throws IOException - thrown if the log file could not be written.
         */
        public CrawlerImporterImplementation() throws IOException {
            Logger logger = Logger.getLogger("MyLog");
            FileHandler fh;
            SimpleDateFormat formats = new SimpleDateFormat("dd-mm-HHMMSS");

            String pathForLogFile = System.getProperty("user.dir");
            System.out.println("Log will be saved at location " + pathForLogFile);
            fh = new FileHandler(pathForLogFile + "/CommandLine_" + formats.format(Calendar.getInstance().getTime()) + ".log");
            logger.addHandler(fh);
            fh.setFormatter(new MyCustomFormatterForLogger());
            logger.setUseParentHandlers(false);
            logger.info("Command Line application started");
            logger.info("Log is saved at location " + System.getProperty("user.home") + "under name CommandLine");
            CommandLineArgumentParser cliParser = new CommandLineArgumentParser();
            // this class is called to get all the different arguments and it's values
            // cliParser.runDifferentArguments(args, logger); // This function will run program depending on flag/argument choosen
        }


        /**
         * Getter for the importFilePath.
         *
         * @return The path to the import file.
         */
        @Override
        public String getImportFilePath() {
            return null;
        }

        /**
         * Setter for the importFilePath.
         *
         * @param filePath The path to the import file.
         */
        @Override
        public void setImportFilePath(final String filePath) {

        }

        /**
         * Imports the data from the file specified by @see Importer.setImportFilePath().
         *
         * @return boolean true if data was imported, false otherwise.
         */
        @Override
        public boolean importData() {
            return false;
        }

        /**
         * Getter for the imported data.
         *
         * @return List of VaultEntry consisting of the imported data.
         * @see VaultEntry
         */
        @Override
        public List<VaultEntry> getImportedData() {
            return null;
        }

        /**
         * Getter for the raw imported data.
         *
         * @return List of RawEntry consisting of the raw imported data.
         * @see RawEntry
         */
        @Override
        public List<RawEntry> getImportedRawData() {
            return null;
        }

        /**
         * Clears all Imported(Raw)Data.
         * {@link Importer#getImportedData()},
         * {@link Importer#getImportedRawData()}
         * will return empty lists afterwards.
         */
        @Override
        public void clearData() {

        }

        /**
         * Method to load the plugin's configuration file.
         *
         * @param configuration the configuration object
         * @return True if configuration can be loaded, false otherwise.
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            return false;
        }

        /**
         * Method to register listeners to the Plugins.
         * The GUI for example can implement onStatusCallback behavior and register its interface here to get notified by a status update.
         *
         * @param listener A listener.
         */
        @Override
        public void registerStatusCallback(final StatusListener listener) {

        }
    }


}
