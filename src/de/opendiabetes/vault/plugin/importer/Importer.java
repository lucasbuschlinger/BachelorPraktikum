package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.logging.Logger;


/**
 * @author Magnus Gärtner
 * @author Lucas Buschlinger
 * This interface specifies the methods shared by all importers
 * It also serves as the {@link org.pf4j.ExtensionPoint} where the plugins hook up
 */
public interface Importer extends ExtensionPoint{

    /**
     * The logger which all importers must have
     */
    Logger LOG = Logger.getLogger(Importer.class.getName());

    void setImportFilePath(String path);

    String getImportFilePath();

    /**
     * Imports the data from the file
     * @return boolean true if data was imported, false otherwise
     */
    boolean importData();

    /**
     * Getter for the imported data
     * @return List of VaultEntry consisting of the imported data
     * @see de.opendiabetes.vault.container.VaultEntry
     */
    List<VaultEntry> getImportedData();

    /**
     * Getter for the raw imported data
     * @return List of RawEntry consisting of the raw imported data
     * @see de.opendiabetes.vault.container.RawEntry
     */
    List<RawEntry> getImportedRawData();

    boolean loadConfiguration(String path);

    void registerStatusCallback(StatusListener listener);

    interface StatusListener {

        void onStatusCallback(int progress, String status);

    }
}
