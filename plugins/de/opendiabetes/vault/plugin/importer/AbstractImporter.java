/**
 * Copyright (C) 2017 OpenDiabetes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Most abstract importer, implements Importer interface.
 * All actual importer plugins are descendants of this class.
 */
public abstract class AbstractImporter implements Importer {

    /**
     * Logger object.
     */
    protected static final  Logger LOG = Logger.getLogger(Importer.class.getName());

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
     * Default Constructor which is used by PluginManagers.
     */
    public AbstractImporter() { }

    /**
     * Imports the data from the path.
     * @return True if data can be imported, false otherwise.
     */
    public abstract boolean importData();

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
     * @param listener
     */
    @Override
    public void registerStatusCallback(final StatusListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Method to notify the registered listeners about progress.
     * @param progress Percentage of completion.
     * @param status Current status.
     */
    protected void notifyStaus(final int progress, final String status) {
        listeners.forEach(listener -> listener.onStatusCallback(progress, status));
    }

}
