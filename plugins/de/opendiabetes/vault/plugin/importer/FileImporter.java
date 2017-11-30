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
package de.opendiabetes.vault.plugin.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * This class defines the default structure how data gets imported from a file.
 * It implements file handling of the data source
 * specified by {@link FileImporter#setImportFilePath(String)}
 * all descendants must implement the template methods
 * {@link FileImporter#preprocessingIfNeeded(String)}
 * {@link FileImporter#processImport(InputStream, String)}
 */
public abstract class FileImporter extends AbstractImporter {

    /**
     * Path to the file data gets imported from.
     */
    private String importFilePath;

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
    public void setImportFilePath(final String path) {
        this.importFilePath = path;
    }

    /**
     * Imports the data from the file specified
     * with {@link FileImporter#setImportFilePath(String)}.
     *
     * @return True if data can be imported, false otherwise.
     */
    public boolean importData() {
        if (null == importFilePath) {
            LOG.log(Level.WARNING, "no path specified from where to import data");
            return false;
        }
        preprocessingIfNeeded(importFilePath);
        this.notifyStaus(0, "preprocessing done.");

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
     * Method for preprocessing the data.//TODO more specific
     *
     * @param filePath Path to the import file.
     */
    protected abstract void preprocessingIfNeeded(String filePath);

    /**
     * //TODO more specific
     * Method for processing the data.
     *
     * @param fileInputStream    The input stream for the imported data.
     * @param filenameForLogging Filename to which the logger should write.
     * @return True if the data can be processed, false otherwise.
     */
    protected abstract boolean processImport(InputStream fileInputStream, String filenameForLogging);

}
