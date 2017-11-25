package de.opendiabetes.vault.plugin.importer.ImportProcessor;

import org.pf4j.ExtensionPoint;

import java.io.FileInputStream;

/**
 * @author magnus
 * ImportProcessor is a module that is normally used to process data during import
 * @see de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter usage example
 */
public interface ImportProcessor extends ExtensionPoint{


    /**
     * Method to process the imported data
     * @param fileImportStream the import data
     * @param logFile the logfile
     * @return true if import data can be processed, false otherwise
     */
    boolean processImport(FileInputStream fileImportStream, String logFile);
}
