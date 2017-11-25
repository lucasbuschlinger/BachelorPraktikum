package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 *
 */

public interface Importer extends ExtensionPoint{

    public abstract boolean importData(String path);

    public List<VaultEntry> getImportedData();

    public List<RawEntry> getImportedRawData();

    //public Logger getLogger();

}
