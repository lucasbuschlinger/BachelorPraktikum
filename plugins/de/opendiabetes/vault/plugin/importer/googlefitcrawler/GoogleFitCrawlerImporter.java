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
package de.opendiabetes.vault.plugin.importer.googlefitcrawler;


import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.List;
import java.util.Properties;

/**
 * Wrapper class for the GoogleFitCrawlerImporter plugin.
 *
 * @author ocastx
 */
public class GoogleFitCrawlerImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public GoogleFitCrawlerImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the Medtronic importer plugin.
     */
    @Extension
    public static class GoogleFitCrawlerImporterImplementation extends CSVImporter {

        /**
         * Constructor.
         */
        public GoogleFitCrawlerImporterImplementation() {
            super(null);
        }


        @Override
        protected void preprocessingIfNeeded(final String filePath) { /*not needed yet*/ }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<VaultEntry> parseEntry(final CsvReader creader) throws Exception {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            return super.loadConfiguration(configuration);
        }
    }


}
