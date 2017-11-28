/**
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
package de.opendiabetes.vault.plugin.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * This class implements the most basic methods used by all importers.
 */
public abstract class FileImporter extends AbstractImporter {

    /**
     * Path to the file data gets imported from.
     */
    private String importFilePath;

    /**
     * Default constructor used by PluginManagers.
     */
    public FileImporter() {

    }

    /**
     * Constructor for a FileImporter.
     * TODO: deprecated?
     * @param path Path to the import file.
     */
    public FileImporter(final String path) {
        this.importFilePath =  path;
    }

    /**
     * Getter for the import file path.
     * @return The path to the import file.
     */
    public String getImportFilePath() {
        return importFilePath;
    }

    /**
     * Setter for the import file path.
     * @param path The path to the import file.
     */
    public void setImportFilePath(final String path) {
        this.importFilePath = path;
    }

    /**
     * Imports the data from the file specified in importFilePath.
     * @return True if data can be imported, false otherwise.
     */
    public boolean importData() {
        if (null != importFilePath) {
            preprocessingIfNeeded(importFilePath);
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(importFilePath);
            return processImport(fis, importFilePath);
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Error opening a FileInputStream for File "
                    + importFilePath, ex);
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Error closing the FileInputStream for File" + importFilePath, ex);
                }
            }
        }

    }

    /**
     * Method for preprocessing the data.
     * @param filePath Path to the import file.
     */
    protected abstract void preprocessingIfNeeded(String filePath);

    /**
     * Method for processing the data.
     * @param fileInputStream The input stream for the imported data.
     * @param filenameForLogging Filename to which the logger should write.
     * @return True if the data can be processed, false otherwise.
     */
    protected abstract boolean processImport(InputStream fileInputStream, String filenameForLogging);

}
