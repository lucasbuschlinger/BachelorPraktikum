/**
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Most abstract importer, implements Importer interface.
 * All actual importer plugins are descendants of this class.
 * supplies a Logger {@link Importer#LOG }
 * handles status listeners {@link AbstractImporter#listeners}
 * @author Magnus Gärtner
 * @author Lucas Buschlinger
 */
public abstract class AbstractImporter implements Importer {

    /**
     * List of VaultEntry which contains the imported and processed data.
     */
    protected List<VaultEntry> importedData;

    /**
     * List of RawEntry which contains the unprocessed data.
     */
    protected List<RawEntry> importedRawData;

    /**
     * List of StatusListener which contains all listeners registered to the importer.
     */
    private List<StatusListener> listeners = new ArrayList<StatusListener>();

    /**
     * {@inheritDoc}
     */
    public List<VaultEntry> getImportedData() {
        return importedData;
    }

    /**
     * {@inheritDoc}
     */
    public List<RawEntry> getImportedRawData() {
        return importedRawData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearData() {
        getImportedData().clear();
        getImportedRawData().clear();
    }

    /**
     * {@inheritDoc}
     * @param listener to be notified on a status update of the plugin
     */
    @Override
    public void registerStatusCallback(final StatusListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Method to notify the registered listeners about progress.
     * see {@link de.opendiabetes.vault.plugin.importer.Importer.StatusListener#onStatusCallback} to register a listener
     * @param progress Percentage of completion.
     * @param status Current status.
     */
    protected void notifyStaus(final int progress, final String status) {
        listeners.forEach(listener -> listener.onStatusCallback(progress, status));
    }
}
