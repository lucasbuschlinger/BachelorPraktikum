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
package de.opendiabetes.vault.plugin.importer.miband;

import de.opendiabetes.vault.plugin.importer.FileImporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.InputStream;
import java.util.Properties;

/**
 * Wrapper class for the MiBandNotifyImporter plugin.
 *
 * @author Lucas Buschlinger
 */
public class MiBandNotifyImporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link PluginWrapper}.
     */
    public MiBandNotifyImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the MiBandNotify importer plugin.
     */
    @Extension
    public static class MiBandNotifyImporterImplementation extends FileImporter {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void preprocessingIfNeeded(final String filePath) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean processImport(final InputStream fileInputStream, final String filenameForLogging) {
           return false;
        }

        /**
         * {@inheritDoc}final
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            return false;
        }

    }
}
