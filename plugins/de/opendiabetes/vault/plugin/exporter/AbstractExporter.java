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

import java.util.ArrayList;
import java.util.List;

/**
 * Most abstract exporter, implements the {@link Exporter} interface.
 * All actual exporter plugins are descendants of this class.
 * Supplies the Logger {@link Exporter#LOG}-
 * Takes care of handling status listeners {@link AbstractExporter#listeners}.
 *
 * @author Lucas Buschlinger
 */
public abstract class AbstractExporter  implements  Exporter {

    /**
     * List holding all StatusListeners registered to the exporter.
     */
    private final List<Exporter.StatusListener> listeners = new ArrayList<>();

    /**
     * The path to the export file.
     */
    private String exportFilePath;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExportFilePath(final String exportPath) {
        this.exportFilePath = exportPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExportFilePath() {
        return exportFilePath;
    }

    /**
     * {@inheritDoc}
     * @param listener A listener to be notified on a status update of the plugin.
     */
    @Override
    public void registerStatusCallback(final StatusListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Notifies the registered {@link de.opendiabetes.vault.plugin.exporter.Exporter.StatusListener}s about progress.
     * See {@link de.opendiabetes.vault.plugin.exporter.Exporter.StatusListener#onStatusCallback} to register listeners.
     *
     * @param progress Percentage of completion.
     * @param status Current status.
     */
    protected void notifyStatus(final int progress, final String status) {
        listeners.forEach(listener -> listener.onStatusCallback(progress, status));
    }
}
