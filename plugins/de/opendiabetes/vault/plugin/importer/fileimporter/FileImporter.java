/*
 * Copyright (C) 2017 OpenDiabetes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer.fileimporter;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.ImportProcessor.ImportProcessor;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.importer.preprocessor.Preprocessor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;


/**
 * The fileimporter Plugin.
 * @author Lucas Buschlinger
 * @author Magnus Gärtner
 */
public class FileImporter extends Plugin {

    /**
     * The constructor for the fileimporter Plugin
     * @param wrapper the plugin wrapper
     */
    public FileImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * The actual implementation of the fileimporter plugin.
     * Implements the Importer interface.
     * @see de.opendiabetes.vault.plugin.importer.Importer
     */
    @Extension
    public static class FileImporterImplementation
            implements Importer {

        /**
         * The path to the import file.
         */
        private String importFilePath;

        /**
         * The imported data in a list of VaultEntry.
         * @see de.opendiabetes.vault.container.VaultEntry
         */
        protected List<VaultEntry> importedData;

        /**
         * The raw imported data in a list of RawEntry
         * @see de.opendiabetes.vault.container.RawEntry
         */
        protected List<RawEntry> importedRawData;

        /**
         * Setter for the import path
         * @param importFilePath path to the import file
         */
        public void setImportFilePath(String importFilePath) {
            this.importFilePath = importFilePath;
        }

        /**
         * Getter for the import path
         * @return importFilePath, path to the import file
         */
        public String getImportFilePath() {
            return importFilePath;
        }

        /**
         * Imports the data from the path
         * @param path Path to the file to be imported
         * @return true if data could be imported, false otherwise
         */
        @Override
        public boolean importData(String path) {
            setImportFilePath(path);
            if(null != preprocessor){
                preprocessor.preprocess(path);
            }

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(importFilePath);
                return importProcessor.processImport(fileInputStream, importFilePath);
            }
            catch (FileNotFoundException exception) {
                LOG.log(Level.SEVERE, "Error opening a FileInputStream for File " + importFilePath, exception);
                return false;
            }
            finally {
                if (fileInputStream != null)
                    try {
                        fileInputStream.close();
                    }
                    catch (Exception exception) {
                        LOG.log(Level.WARNING, "Error closing the FileInputStream for File " + importFilePath,
                                exception);
                    }
            }
        }

        @Override
        public List<VaultEntry> getImportedData() {
            return importedData;
        }

        @Override
        public List<RawEntry> getImportedRawData() {
            return importedRawData;
        }

        /**
         * Preprocessor used to preprocess the data before the actual import
         * @see Preprocessor
         */
        protected Preprocessor preprocessor;

        /**
         * actual processing unit that imports data
         */
        protected ImportProcessor importProcessor;
    }
}
