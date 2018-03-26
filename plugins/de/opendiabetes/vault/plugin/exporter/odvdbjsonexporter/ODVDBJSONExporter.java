/*
 * Copyright (C) 2017 juehv
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
package de.opendiabetes.vault.plugin.exporter.odvdbjsonexporter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.container.csv.ODVDBJSONPseudoEntry;
import de.opendiabetes.vault.plugin.exporter.FileExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class for the ODVDBJSONExporter plugin.
 *
 * @author juehv
 */
public class ODVDBJSONExporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public ODVDBJSONExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the ODVDBJSONExporter.
     */
    @Extension
    public static final class ODVDBJSONExporterImplementation extends FileExporter<ExportEntry, VaultEntry> {


        /**
         * {@inheritDoc}
         */
        @Override
        protected List<ExportEntry> prepareData(final List<VaultEntry> data) throws IllegalArgumentException {
            if (data == null || data.isEmpty()) {
                LOG.log(Level.SEVERE, "Data cannot be empty");
                throw new IllegalArgumentException("Data cannot be empty");
            }
            List<ExportEntry> container = new ArrayList<>();
            List<VaultEntry> tempData;
            if (getIsPeriodRestricted()) {
               tempData = filterPeriodRestriction((List<VaultEntry>) data);
            } else {
                tempData = (List<VaultEntry>) data;
            }
            container.add(ODVDBJSONPseudoEntry.fromVaultEntryList(tempData));
            return container;
        }

    }
}
