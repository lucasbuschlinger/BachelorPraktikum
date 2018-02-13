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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import static java.lang.Boolean.parseBoolean;

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
    public static class SliceLayoutCSVExporterImplementation extends CSVFileExporter {

        /**
         * The entries to be exported by the SliceLayoutCSVExporter plugins.
         */
        private List<SliceEntry> entries;

        /**
         * Setter for the {@link SliceLayoutCSVExporterImplementation#entries}.
         *
         * @param entries The entries to be set.
         */
        private void setEntries(final List<SliceEntry> entries) {
            this.entries = entries;
        }

        /**
         * This implementation sets the list of {@link SliceEntry} to be exported.
         *
         * @param object The list of {@link SliceEntry} to be set.
         * @throws IllegalArgumentException Thrown if the object passed is not an instance of {@link List}.
         */
        @Override
        public void setAdditional(final Object object) {
            if (object instanceof List) {
                setEntries((List<SliceEntry>) object);
            } else {
                throw new IllegalArgumentException("Wrong argument given, only objects of type List<SliceEntry> are applicable!");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<ExportEntry> prepareData(final List<VaultEntry> data) {
            List<ExportEntry> retVal = new ArrayList<>();
            for (SliceEntry item : entries) {
                retVal.add(new SliceCsVEntry(item));
            }
            return retVal;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            if (!super.loadConfiguration(configuration)) {
                return false;
            }
            // Status update constant
            final int loadConfigProgress = 0;
            // Format of dates which must be used.
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            this.notifyStatus(loadConfigProgress, "Loading configuration");

            if (!configuration.containsKey("periodRestriction")
                    || configuration.getProperty("periodRestriction") == null
                    || configuration.getProperty("periodRestriction").length() == 0) {
                LOG.log(Level.WARNING, "SliceCSVExporter configuration does not specify whether the data is period restricted");
                return false;
            }
            boolean restriction = parseBoolean(configuration.getProperty("periodRestriction"));
            this.setIsPeriodRestricted(restriction);

            // Only necessary to look for dates if data is period restricted
            if (restriction) {
                Date dateFrom;
                Date dateTo;
                String startDate = configuration.getProperty("periodRestrictionFrom");
                String endDate = configuration.getProperty("periodRestrictionTo");
                if (startDate == null || endDate == null) {
                    LOG.log(Level.SEVERE, "SliceCSVExporter configuration specified a period restriction on the data but no correct"
                            + " dates were specified.");
                    return false;
                }
                // Parsing to actual dates
                try {
                    dateFrom = dateFormat.parse(startDate);
                    dateTo = dateFormat.parse(endDate);
                } catch (ParseException exception) {
                    LOG.log(Level.SEVERE, "Either of the dates specified in the SliceCSVExporter config is malformed."
                            + " The expected format is dd/mm/yyyy.");
                    return false;
                }

                // Check whether the start time lies before the end time
                if (dateFrom.after(dateTo)) {
                    LOG.log(Level.WARNING, "The date the data is period restricted from lies after the date it is restricted to,"
                            + " check order.");
                    return false;
                }

                this.setExportPeriodFrom(dateFrom);
                this.setExportPeriodTo(dateTo);
                LOG.log(Level.INFO, "Data is period restricted from " + dateFrom.toString() + " to " + dateTo.toString());
                return true;
            } else {
                LOG.log(Level.INFO, "Export data is not period restricted by SliceCSVExporter configuration.");
                return true;
            }
        }
    }
}
