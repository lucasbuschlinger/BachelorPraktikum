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
import de.opendiabetes.vault.container.csv.OdvDbJsonPseudoEntry;
import de.opendiabetes.vault.plugin.exporter.FileExporter;
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
 * Wrapper class for the OdvDbJsonExporter plugin.
 *
 * @author juehv
 */
public class OdvDbJsonExporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public OdvDbJsonExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the OdvDbJsonExporter.
     */
    @Extension
    public static class OdvDbJsonExporterImplementation extends FileExporter {

        /**
         * Prepares data for the export by putting it into exportable containers.
         *
         * @param data The data to be prepared.
         * @return The data in exportable containers.
         */
        @Override
        protected List<ExportEntry> prepareData(final List<VaultEntry> data) {
            List<ExportEntry> container = new ArrayList<>();
            container.add(OdvDbJsonPseudoEntry.fromVaultEntryList(data));
            return container;
        }

        /**
         * Setter for an object used by the specific exporters.
         * The implementation of this should only call a private setter method like
         * {@link de.opendiabetes.vault.plugin.exporter.VaultExporter#setDatabase(VaultDao)}.
         * in the exporters and pass the object to those.
         *
         * @param object The object to be set.
         * @throws IllegalArgumentException Thrown if a wrong object gets passed (for example if a {@link VaultDao} object gets passed
         *                                  to a {@link de.opendiabetes.vault.plugin.exporter.slicelayoutcsv.SliceLayoutCSVExporter} which
         *                                  only takes a list of {@link de.opendiabetes.vault.container.SliceEntry}.
         */
        @Override
        public void setAdditional(final Object object) throws IllegalArgumentException {
            LOG.log(Level.WARNING, "Unpossible to set additional for OdvDbJsonExporter because "
                    + "the exporter does not have any additional objects to overwrite.");
        }

        /**
         * Loads the configuration for the exporter plugin.
         *
         * @param configuration The configuration object.
         * @return True if configuration can be loaded, false otherwise.
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
                LOG.log(Level.WARNING, "OdvDbJsonExporter configuration does not specify whether the data is period restricted");
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
                    LOG.log(Level.SEVERE, "OdvDbJsonExporter configuration specified a period restriction on the data but no correct"
                            + " dates were specified.");
                    return false;
                }
                // Parsing to actual dates
                try {
                    dateFrom = dateFormat.parse(startDate);
                    dateTo = dateFormat.parse(endDate);
                } catch (ParseException exception) {
                    LOG.log(Level.SEVERE, "Either of the dates specified in the OdvDbJsonExporter config is malformed."
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
                LOG.log(Level.INFO, "Export data is not period restricted by OdvDbJsonExporter configuration.");
                return true;
            }
        }
    }
}
