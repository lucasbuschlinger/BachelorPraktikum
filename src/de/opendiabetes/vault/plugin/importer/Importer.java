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
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.logging.Logger;


/**
 * @author Magnus Gärtner
 * @author Lucas Buschlinger
 * This interface specifies the methods shared by all importers.
 * It also serves as the {@link org.pf4j.ExtensionPoint} where the plugins hook up
 */
public interface Importer extends ExtensionPoint {

    /**
     * The logger which all importers must have.
     */
    Logger LOG = Logger.getLogger(Importer.class.getName());

    /**
     * Getter for the importFilePath.
     * @return The path to the import file.
     */
    String getImportFilePath();

    /**
     * Setter for the importFilePath.
     * @param path The path to the import file.
     */
    void setImportFilePath(String path);

    /**
     * Imports the data from the file.
     * @return boolean true if data was imported, false otherwise
     */
    boolean importData();

    /**
     * Getter for the imported data.
     * @return List of VaultEntry consisting of the imported data.
     * @see de.opendiabetes.vault.container.VaultEntry
     */
    List<VaultEntry> getImportedData();

    /**
     * Getter for the raw imported data.
     * @return List of RawEntry consisting of the raw imported data.
     * @see de.opendiabetes.vault.container.RawEntry
     */
    List<RawEntry> getImportedRawData();

    /**
     * Method to load the plugin's configuration file.
     * @param path Path to the configuration file.
     * @return True if configuration can be loaded, false otherwise.
     */
    boolean loadConfiguration(String path);

    /**
     * TODO: better
     * Method to register listeners to the Plugins.
     * @param listener A listener.
     */
    void registerStatusCallback(StatusListener listener);

    /**
     * Interface which defines the callback functionality.
     */
    interface StatusListener {

        /**
         * TODO: ?.
         * @param progress Percentage of completion.
         * @param status Current Status.
         */
        void onStatusCallback(int progress, String status);

    }
}
