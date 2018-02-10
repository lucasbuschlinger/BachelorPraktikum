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
package de.opendiabetes.vault.plugin.common;

import java.util.Properties;
import java.util.logging.Logger;

/**
 *@author magnus
 */
public interface OpenDiabetesPlugin {
    /**
     * Logger object of all Plugins. Logs all messages of the plugins to a human readable file.
     */
    Logger LOG = Logger.getLogger(OpenDiabetesPlugin.class.getName());

    /**
     *
     * @param configuration
     * @return
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
         * Is called multiple times on all listeners during processing to notify them about the current progress.
         *
         * @param progress Percentage of completion.
         * @param status   Current Status.
         */
        void onStatusCallback(int progress, String status);
    }
}
