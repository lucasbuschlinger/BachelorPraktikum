package de.opendiabetes.vault.plugin.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractPlugin implements  OpenDiabetesPlugin{

    /**
     * List of StatusListener which contains all listeners registered to the importer.
     */
    private List<StatusListener> listeners = new ArrayList<StatusListener>();

    /**
     * {@inheritDoc}
     *
     * @param listener to be notified on a status update of the plugin.
     */
    @Override
    public void registerStatusCallback(final StatusListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Method to notify the registered listeners about progress.
     * See {@link de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin.StatusListener#onStatusCallback} to register a listener.
     *
     * @param progress Percentage of completion.
     * @param status   Current status.
     */
    protected void notifyStatus(final int progress, final String status) {
        listeners.forEach(listener -> listener.onStatusCallback(progress, status));
    }
}
