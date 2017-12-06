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
package de.opendiabetes.vault.plugin.importer.googlefit;


import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.validator.GoogleFitCSVValidator;
import de.opendiabetes.vault.plugin.util.EasyFormatter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class for the GoogleFitCSVImporter plugin.
 *
 * @author Magnus Gärtner
 */
public class GoogleFitCSVImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public GoogleFitCSVImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the Medtronic importer plugin.
     */
    @Extension
    public static class GoogleFitCSVImporterImplementation extends CSVImporter {

        /**
         * Constructor.
         */
        public GoogleFitCSVImporterImplementation() {
            super(new GoogleFitCSVValidator(), ',');
        }


        @Override
        protected void preprocessingIfNeeded(final String filePath) { /*not needed yet*/ }

        @Override
        protected List<VaultEntry> parseEntry(final CsvReader creader) throws Exception {
            List<VaultEntry> retVal = new ArrayList<>();
            GoogleFitCSVValidator parseValidator = (GoogleFitCSVValidator) getValidator();

            long walkTime = parseValidator.getWalkValue(creader);
            long runTime = parseValidator.getRunValue(creader);
            long bikeTime = parseValidator.getBikeValue(creader);

            // basic filtering of idle entries
            // --> relevant activity has to be more than 1m
            if ((runTime + bikeTime + walkTime) < 1000) {
                return retVal;
            }

            VaultEntry newVaultEntry;
            Date timestamp = new Date(parseValidator.getStartTime(creader, getImportFilePath()));
            double durationInMinutes = Math.round((runTime + bikeTime + walkTime) / 1000 / 60);
            double maxSpeed = parseValidator.getMaxSpeedValue(creader);

            // estimate the activity within this slot
            if (runTime > bikeTime && runTime > walkTime) {
                newVaultEntry = new VaultEntry(VaultEntryType.EXERCISE_RUN,
                        timestamp, durationInMinutes);
                newVaultEntry.setValue2(maxSpeed);
                newVaultEntry.addAnnotation((new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_GoogleRun))
                        .setValue(EasyFormatter.formatDouble(durationInMinutes)));
                retVal.add(newVaultEntry);
            } else if (bikeTime > runTime && bikeTime > walkTime) {
                newVaultEntry = new VaultEntry(VaultEntryType.EXERCISE_BICYCLE,
                        timestamp, durationInMinutes);
                newVaultEntry.setValue2(maxSpeed);
                newVaultEntry.addAnnotation((new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_GoogleBicycle))
                        .setValue(EasyFormatter.formatDouble(durationInMinutes)));
                retVal.add(newVaultEntry);
            } else {
                newVaultEntry = new VaultEntry(VaultEntryType.EXERCISE_WALK,
                        timestamp, durationInMinutes);
                newVaultEntry.setValue2(maxSpeed);
                newVaultEntry.addAnnotation((new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.EXERCISE_GoogleWalk))
                        .setValue(EasyFormatter.formatDouble(durationInMinutes)));
                retVal.add(newVaultEntry);
            }

            return retVal;
        }

        @Override
        public boolean loadConfiguration(final String filePath) {
            LOG.log(Level.WARNING, "GoogleFitCSVImporter does not support configuration.");
            return false;
        }
    }


}
