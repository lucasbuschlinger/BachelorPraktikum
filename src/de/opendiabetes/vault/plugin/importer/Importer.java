/*
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
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * @author Magnus Gärtner
 * @author Lucas Buschlinger
 * This interface specifies the methods shared by all importers.
 * It also serves as the {@link org.pf4j.ExtensionPoint} where the plugins hook up.
 * Therefore all importer plugins must implement this interface to get recognized as importer.
 */
public interface Importer extends ExtensionPoint {

    /**
     * Logger object of all Importers. Logs all messages of the importers to a human readable file.
     */
    Logger LOG = Logger.getLogger(Importer.class.getName());


    /**
     * Getter for the importFilePath.
     *
     * @return The path to the import file.
     */
    String getImportFilePath();

    /**
     * Setter for the importFilePath.
     *
     * @param filePath The path to the import file.
     */
    void setImportFilePath(String filePath);

    /**
     * Imports the data from the file specified by @see Importer.setImportFilePath().
     *
     * @return boolean true if data was imported, false otherwise.
     */
    boolean importData();

    /**
     * Getter for the imported data.
     *
     * @return List of VaultEntry consisting of the imported data.
     * @see de.opendiabetes.vault.container.VaultEntry
     */
    List<VaultEntry> getImportedData();

    /**
     * Getter for the raw imported data.
     *
     * @return List of RawEntry consisting of the raw imported data.
     * @see de.opendiabetes.vault.container.RawEntry
     */
    List<RawEntry> getImportedRawData();

    /**
     * Clears all Imported(Raw)Data.
     * {@link Importer#getImportedData()},
     * {@link Importer#getImportedRawData()}
     * will return empty lists afterwards.
     */
    void clearData();

    /**
     * Method to load the plugin's configuration file.
     *
     * @param configuration the configuration object
     * @return True if configuration can be loaded, false otherwise.
     */
    boolean loadConfiguration(Properties configuration);

    /**
     *
     * @return a html formatted text, that gets displayed to the user if he wants to know more about that plugin.
     */
    String getHelp();

    /**
     * Method to register listeners to the Plugins.
     * The GUI for example can implement onStatusCallback behavior and register its interface here to get notified by a status update.
     *
     * @param listener A listener.
     */
    void registerStatusCallback(StatusListener listener);

    /**
     * Interface which defines the methods called on a status update.
     * Must be implemented by any listener of this plugin to handle the status update.
     */
    interface StatusListener {
        /**
         * Is called multiple times on all listeners during the import process to notify them about the import progress.
         *
         * @param progress Percentage of completion.
         * @param status   Current Status.
         */
        void onStatusCallback(int progress, String status);
    }
}
