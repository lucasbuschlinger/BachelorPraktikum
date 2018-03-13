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
package de.opendiabetes.vault.plugin.exporter.slicelayoutcsv;

import de.opendiabetes.vault.container.SliceEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.container.csv.SliceCsVEntry;
import de.opendiabetes.vault.plugin.exporter.CSVFileExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class for the SliceLayoutCSVExporter plugin.
 *
 * @author Lucas Buschlinger
 */
public class SliceLayoutCSVExporter extends Plugin {

    /**
     * Constructor used by the {@link org.pf4j.PluginManager} to instantiate.
     *
     * @param wrapper The {@link PluginWrapper}.
     */
    public SliceLayoutCSVExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the SliceLayoutCSVExporter.
     */
    @Extension
    public static final class SliceLayoutCSVExporterImplementation extends CSVFileExporter {
        /**
         * {@inheritDoc}
         */
        @Override
        protected <T> List<ExportEntry> prepareData(final List<T> data, final Class<T> listEntryType) throws UnsupportedDataTypeException{
            if (!SliceEntry.class.isAssignableFrom(listEntryType)){
                throw new UnsupportedDataTypeException("SliceLayoutCSV exporter accepts only List<SliceEntrys> data");
            }
            List<ExportEntry> retVal = new ArrayList<>();
            for (T item : data) {
                retVal.add(new SliceCsVEntry((SliceEntry) item));
            }
            return retVal;
        }

    }
}
