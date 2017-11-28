package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractImporter implements Importer {

    protected final static Logger LOG = Logger.getLogger(Importer.class.getName());
    protected List<VaultEntry> importedData;
    protected List<RawEntry> importedRawData;
    private List<StatusListener> listeners = new ArrayList<StatusListener>();
    private Path path;

    public AbstractImporter() {
    }

    public abstract boolean importData();/*//@Override
    public void setImportFilePath(String path) {
        this.path = Paths.get(path);
    }*/

    public List<VaultEntry> getImportedData() {
        return importedData;
    }

    public List<RawEntry> getImportedRawData() {
        return importedRawData;
    }

    @Override
    public void registerStatusCallback(StatusListener listener) {
        this.listeners.add(listener);
    }

    protected void notifyStaus(int progress, String status) {
        listeners.forEach(listener -> listener.onStatusCallback(progress, status));
    }

}
