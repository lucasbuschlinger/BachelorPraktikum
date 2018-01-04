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
package de.opendiabetes.vault.plugin.exporter;

import de.opendiabetes.vault.container.VaultEntry;
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This interface specifies the methods shared by all exporters.
 * Furthermore it serves as the {@link org.pf4j.ExtensionPoint} where the plugins hook up.
 * Thus all exporter plugins must implement this interface to get recognized as exporters.
 *
 * @author Lucas Buschlinger
 */
public interface Exporter extends ExtensionPoint {

    /**
     * Logger object of all exporters. Logs all messages of the exporters to a human readable file.
     */
    Logger LOG = Logger.getLogger(Exporter.class.getName());

    /**
     * Return codes for exporting data.
     */
    enum ReturnCode {
        /**
         * Indicates that everything went right.
         */
        RESULT_OK(0),
        /**
         * Indicates that something went wrong.
         */
        RESULT_ERROR(-1),
        /**
         * Indicates that no data was specified to be exported.
         */
        RESULT_NO_DATA(-2),
        /**
         * Indicates an error accessing the export file.
         */
        RESULT_FILE_ACCESS_ERROR(-3);

        /**
         * The numeric representation of the return codes.
         */
        private final int code;

        /**
         * Constructor.
         *
         * @param code The numeric value of the return code.
         */
        ReturnCode(final int code) {
            this.code = code;
        }

        /**
         * Getter for the numeric representation of the return codes.
         * @return The numeric code associated to the ReturnCode this gets called on.
         */
        public int getCode() {
            return code;
        }
    }

    /**
     * Setter for the exportFilePath.
     *
     * @param filePath The path to the export file.
     */
    void setExportFilePath(String filePath);

    /**
     * Getter for the exportFilePath.
     *
     * @return The exportFilePath.
     */
    String getExportFilePath();

    /**
     * Setter for an object used by the specific exporters.
     * The implementation of this should only call a private setter method like
     * {@link VaultExporter#setDatabase(de.opendiabetes.vault.data.VaultDao)}.
     * in the exporters and pass the object to those.
     *
     * @param object The object to be set.
     * @throws IllegalArgumentException Thrown if a wrong object gets passed (for example if a
     *                                     {@link de.opendiabetes.vault.data.VaultDao} object gets passed
     *                                     to a {@link de.opendiabetes.vault.plugin.exporter.slicelayoutcsv.SliceLayoutCSVExporter} which
     *                                     only takes a list of {@link de.opendiabetes.vault.container.SliceEntry}.
     */
    void setAdditional(Object object) throws IllegalArgumentException;

    /**
     * Exports the data to a file.
     *
     * @param data The data to export.
     * @return The return code as specified in {@link ReturnCode}.
     */
    int exportDataToFile(List<VaultEntry> data);

    /**
     * Loads the configuration for the exporter plugin.
     *
     * @param configuration The configuration object.
     * @return True if configuration can be loaded, false otherwise.
     */
    boolean loadConfiguration(Properties configuration);

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
         * Is called multiple times on all listeners during the export process to notify them about the import progress.
         *
         * @param progress Percentage of completion.
         * @param status   Current Status.
         */
        void onStatusCallback(int progress, String status);
    }
}
