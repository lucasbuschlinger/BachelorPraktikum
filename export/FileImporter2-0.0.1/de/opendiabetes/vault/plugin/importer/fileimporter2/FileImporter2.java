package de.opendiabetes.vault.plugin.importer.fileimporter2;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.List;


public class FileImporter2 extends Plugin {

    public FileImporter2(PluginWrapper wrapper) {
        super(wrapper);

    }

    @Extension
    public static class FileImporterImplementation implements Importer {


        @Override
        public void setImportFilePath(String importFilePath) {

        }

        @Override
        public String getImportFilePath() {
            return null;
        }

        @Override
        public boolean importData(String path) {
            System.out.println("fileimporter2: importing data from: " + path);
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

}
