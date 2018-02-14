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
package de.opendiabetes.vault.plugin.importer.medtroniccrawler;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.pf4j.Extension;
import org.pf4j.PluginManager;
import org.pf4j.DefaultPluginManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Wrapper class for the MedtronicCrawlerImporter plugin.
 */
public class MedtronicCrawlerImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public MedtronicCrawlerImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the MedtronicCrawlerImporter plugin.
     */
    @Extension
    public static class MedtronicCrawlerImporterImplementation implements Importer {

        /**
         * Constructor.
         *
         * @throws IOException - thrown if the log file could not be written.
         */
        public MedtronicCrawlerImporterImplementation() throws IOException {
            Logger logger = Logger.getLogger("MyLog");
            FileHandler fh;
            SimpleDateFormat formats = new SimpleDateFormat("dd-mm-HHMMSS");

            String pathForLogFile = System.getProperty("user.dir");
            System.out.println("Log will be saved at location " + pathForLogFile);
            fh = new FileHandler(pathForLogFile + "/CommandLine_" + formats.format(Calendar.getInstance().getTime()) + ".log");
            logger.addHandler(fh);
            fh.setFormatter(new CrawlerLogFormatter());
            logger.setUseParentHandlers(false);
            logger.info("Command Line application started");

            String username = "";
            String password = "";

            Authentication auth = new Authentication();
            if (!auth.checkConnection(username, password, logger)) {
                logger.info("username and password entered are incorrect");
                return;
            }
            String lang = auth.getLanguage();

            String fromDate = "";
            String toDate = "";

            try {
                DateHelper dateHelper = new DateHelper(lang);
                if (!dateHelper.getStartDate(fromDate, logger)) {
                    logger.info("from date is incorrect");
                    return;
                }
                logger.info("from date is correct");

                if (!dateHelper.getEndDate(fromDate, toDate, logger)) {
                    logger.info("End date is incorrect");
                    return;
                }

                logger.info("End date is correct");
            } catch (ParseException e) {
                logger.info("Parse exception");
                return;
            }

            Crawler crawler = new Crawler();

            String userHomepath = System.getProperty("user.dir");
            crawler.generateDocument(auth.getCookies(), fromDate, toDate, userHomepath, logger);

            String path = userHomepath + File.separator + "careLink-Export";

            // TODO change to dynamic path
            PluginManager manager = new DefaultPluginManager(Paths.get("export"));
            manager.loadPlugins();
            manager.enablePlugin("MedtronicImporter");
            manager.startPlugin("MedtronicImporter");
            Importer medtronicImporter = manager.getExtensions(Importer.class).get(0);
            medtronicImporter.setImportFilePath(path);
            medtronicImporter.importData();
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
         * @return a path to a file containing .md/html formatted text,
         * that gets displayed to the user if he wants to know more about that plugin.
         */
        @Override
        public String getHelpFilePath() {
            //TODO not implemented yet
            return null;
        }

        /**
         * Takes the list of compatible plugins from a configuration file and returns it.
         *
         * @return a list of plugin names that are known to be compatible with this plugin
         * @see {@link AbstractPlugin#loadConfiguration(Properties)} {@link AbstractPlugin#getListOfCompatiblePluginIDs()}
         * for an implementation.
         */
        @Override
        public List<String> getListOfCompatiblePluginIDs() {
            List<String> pluginIds = new ArrayList<>();
            pluginIds.add("MedtronicImporter");
            return pluginIds;
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
