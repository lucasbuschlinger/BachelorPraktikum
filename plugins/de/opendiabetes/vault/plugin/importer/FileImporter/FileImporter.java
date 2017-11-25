package de.opendiabetes.vault.plugin.importer.FileImporter;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

/**
 * The FileImporter Plugin
 * @author Lucas Buschlinger
 * @author Magnus Gärtner
 */
public class FileImporter extends Plugin {

    /**
     * The constructor for the FileImporter Plugin
     * @param wrapper the plugin wrapper
     */
    public FileImporter(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * The actual implementation of the FileImporter plugin
     * Implements the Importer interface
     * @see de.opendiabetes.vault.plugin.importer.Importer
     */
    @Extension
    public abstract class FileImporterImplementation implements Importer {

        /**
         * The path to the import file
         */
        protected  String importFilePath;

        /**
         * The imported data in a list of VaultEntry
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
            preprocessingIfNeeded(importFilePath);

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(importFilePath);
                return processImport(fileInputStream, importFilePath);
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
         * Method to do preprocessing on the imported data, if necessary
         * @param filePath path to the import file
         */
        protected  abstract void preprocessingIfNeeded(String filePath);

        /**
         * Method to process the imported data
         * @param fileImportStream the import data
         * @param logFile the logfile
         * @return true if import data can be processed, false otherwise
         */
        protected abstract boolean processImport(InputStream fileImportStream, String logFile);
    }
}

/*
public class FileImporter extends Plugin {

    public FileImporter(PluginWrapper wrapper) {
        super(wrapper);

    }

    @Extension
    public static class FileImporterImplementation implements Importer {


        @Override
        public boolean importData(String path) {
            System.out.println("importing data from: " + path);
            return true;
        }

        @Override
        public List<VaultEntry> getImportedData() {
            return null;
        }

        @Override
        public List<RawEntry> getImportedRawData() {
            return null;
        }
    }

}*/
