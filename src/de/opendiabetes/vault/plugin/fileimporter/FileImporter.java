package de.opendiabetes.vault.plugin.fileimporter;

import de.opendiabetes.vault.plugin.importer.Importer;

/**
 * @author ocastx
 * This interface specifies the methods shared by all file importers.
 * It also serves as the {@link org.pf4j.ExtensionPoint} where the plugins hook up.
 * Therefore all file importer plugins must implement this interface to get recognized as file importer.
 */
public interface FileImporter extends Importer {

    /**
     * Imports the data from a file.
     *
     * @param filePath path to the file which should be used for importing data
     * @return boolean true if data was imported, false otherwise.
     */
    boolean importData(String filePath);
}
