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

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.common.AbstractPlugin;
import de.opendiabetes.vault.plugin.importer.crawlerimporter.CrawlerImporter;
import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.pf4j.Extension;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

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
    public static class MedtronicCrawlerImporterImplementation extends AbstractPlugin implements CrawlerImporter {

        /**
         * Progress percentage for showing that the configuration has been loaded.
         */
        private static final int PROGRESS_CONFIG_LOADED = 25;

        /**
         * Date string from when the data should start.
         */
        private String fromDate;

        /**
         * Date string until when the data should be imported.
         */
        private String toDate;

        /**
         * {@inheritDoc}
         */
        @Override
        public List<VaultEntry> importData(final String username, final String password) throws Exception {

            Authentication auth = new Authentication();
            if (!auth.checkConnection(username, password)) {
                LOG.log(Level.SEVERE, "Entered username/password are incorrect");
                throw new Exception("Entered username/password are incorrect");
            }
            String lang = auth.getLanguage();

            try {
                DateHelper dateHelper = new DateHelper(lang);
                if (!dateHelper.getStartDate(fromDate)) {
                    LOG.log(Level.SEVERE, "fromDate is incorrect");
                    throw new IllegalArgumentException("fromDate is incorrect");
                }

                if (!dateHelper.getEndDate(fromDate, toDate)) {
                    LOG.log(Level.SEVERE, "toDate is incorrect");
                    throw new IllegalArgumentException("toDate is incorrect");
                }
            } catch (ParseException exception) {
                LOG.log(Level.SEVERE, "Date parsing failed");
                throw exception;
            }

            Crawler crawler = new Crawler();

            String exportPath = Paths.get(System.getProperty("java.io.tmpdir"), "MedtronicCrawler").toString();
            crawler.generateDocument(auth.getCookies(), fromDate, toDate, exportPath);


            String path = exportPath + File.separator + "careLink-Export";

            OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();

            FileImporter plugin = manager.getPluginFromString(FileImporter.class, "MedtronicImporter");

            if (plugin == null) {
                LOG.log(Level.SEVERE, "Plugin MedtronicImporter not found");
                throw new Exception("Plugin MedtronicImporter not found");
            }

            return plugin.importData(path);
        }
        /**
         * Template method to load plugin specific configurations from the config file.
         *
         * @param configuration The configuration object.
         * @return whether a valid configuration could be read from the config file
         */
        @Override
        protected boolean loadPluginSpecificConfiguration(final Properties configuration) {
            if (configuration.containsKey("fromDate")) {
                this.fromDate = configuration.getProperty("fromDate");
            }
            if (configuration.containsKey("toDate")) {
                this.toDate = configuration.getProperty("toDate");
            }
            this.notifyStatus(PROGRESS_CONFIG_LOADED, "Loaded configuration");
            return false;
        }
    }
}
