package de.opendiabetes.vault.plugin.importer.FileImporter;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.List;


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

}
