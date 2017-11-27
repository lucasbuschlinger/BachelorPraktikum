package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.plugin.importer.AbstractImporter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

public abstract class FileImporter extends AbstractImporter {

    protected String importFilePath;

    public FileImporter(String importFilePath) {
        this.importFilePath = importFilePath;
    }

    public String getImportFilePath() {
        return importFilePath;
    }

    public void setImportFilePath(String importFilePath) {
        this.importFilePath = importFilePath;
    }

    public boolean importData() {
        preprocessingIfNeeded(importFilePath);

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
                    // not interesting :D
                }
            }
        }

    }

    protected abstract void preprocessingIfNeeded(String filePath);

    protected abstract boolean processImport(InputStream fis, String filenameForLogging);

}
