package de.opendiabetes.vault.plugin.importer.preprocessor;

import de.opendiabetes.vault.plugin.importer.FileImporter;
import org.pf4j.ExtensionPoint;

/**
 * @author magnus
 * Preprocessor is a module that is normally used in a importer module
 * @see FileImporter usage example
 */
public interface Preprocessor extends ExtensionPoint {
    /**
     * do preprocessing on the imported data
     *
     * @param path to the import file
     */
    void preprocess(String path);
}
