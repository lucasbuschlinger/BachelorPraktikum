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
package de.opendiabetes.vault.plugin.importer.googlefitcrawler;


import com.csvreader.CsvReader;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.AbstractImporter;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.fitness.GoogleFitness;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.helper.Credentials;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.javaFX.views.ConflictedLocations;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.location.GooglePlaces;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.location.LocationHistory;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.people.GooglePeople;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.plot.GoogleMapsPlot;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.plot.Plotter;
import org.jfree.ui.IntegerDocument;
import org.jfree.util.BooleanUtilities;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

/**
 * Wrapper class for the GoogleFitCrawlerImporter plugin.
 *
 * @author ocastx
 */
public class GoogleFitCrawlerImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public GoogleFitCrawlerImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the Medtronic importer plugin.
     */
    @Extension
    public static class GoogleFitCrawlerImporterImplementation extends AbstractImporter {

        private String clientSecretPath;
        private String apiKey;
        private int age;
        private String timeframe;
        private String[] keywordSearchParams;
        private boolean exportHistory;

        private String plotTimeframe;
        private boolean exportPlot;
        private boolean viewPlot;
        private boolean viewMap;

        /**
         * Constructor.
         */
        public GoogleFitCrawlerImporterImplementation() {


        }

        /**
         * Getter for the importFilePath.
         *
         * @return The path to the import file.
         */
        @Override
        public String getImportFilePath() {
            return null;
        }

        /**
         * Setter for the importFilePath.
         *
         * @param filePath The path to the import file.
         */
        @Override
        public void setImportFilePath(String filePath) { /* not needed for now */ }

        /**
         * Imports the data from the file specified by @see Importer.setImportFilePath().
         *
         * @return boolean true if data was imported, false otherwise.
         */
        @Override
        public boolean importData() {

            Credentials credentialsInstance = Credentials.getInstance();

            try {

                if (clientSecretPath != null) {
                    credentialsInstance.authorize(clientSecretPath);
                }

                if (apiKey != null) {
                    credentialsInstance.setAPIkey(apiKey);
                }

                LocationHistory.getInstance().setAge(age);
                GooglePeople.getInstance().getAllProfiles();

                if (keywordSearchParams != null) {
                    GooglePlaces.getInstance().setKeywordSearchParams(keywordSearchParams);
                }

                if (timeframe != null) {
                    Calendar start = new GregorianCalendar();
                    Calendar end = new GregorianCalendar();

                    if (timeframe.equals("all")) {
                        start.set(2014, Calendar.JANUARY, 1, 0, 0, 0);
                        start.set(Calendar.MILLISECOND, 0);
                        end = GregorianCalendar.getInstance();


                        System.out.println("set all as timeframe");
                    } else if (timeframe.contains("-")) {
                        String[] help = timeframe.split("-");

                        String[] startDate = help[0].split("\\.");
                        start.set(Integer.parseInt(startDate[2]), Integer.parseInt(startDate[1]) - 1, Integer.parseInt(startDate[0]), 0, 0, 0);
                        start.set(Calendar.MILLISECOND, 0);

                        String[] endDate = help[1].split("\\.");
                        end.set(Integer.parseInt(endDate[2]), Integer.parseInt(endDate[1]) - 1, Integer.parseInt(endDate[0]), 0, 0, 0);
                        end.set(Calendar.MILLISECOND, 0);
                    } else {
                        String[] date = timeframe.split("\\.");
                        start.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), 0, 0, 0);
                        start.set(Calendar.MILLISECOND, 0);

                        end.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), 0, 0, 0);
                        end.set(Calendar.MILLISECOND, 0);
                    }

                    GoogleFitness.getInstance().getData(start.getTimeInMillis(), end.getTimeInMillis());
                }


                if (exportHistory)
                    LocationHistory.getInstance().export();


                if (plotTimeframe != null) {
                    Plotter plot = new Plotter(plotTimeframe);

                    if (exportPlot)
                        plot.export();

                    if (viewPlot)
                        plot.viewPlot();

                }

                if (viewMap) {
                    GoogleMapsPlot.getInstance().createMap();
                    GoogleMapsPlot.getInstance().openMap();
                }

                if (!LocationHistory.getInstance().getConflictedActivities().isEmpty())
                    ConflictedLocations.main(new String[]{});

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            if (configuration.containsKey("clientSecretPath")) {
                clientSecretPath = configuration.getProperty("clientSecretPath");
            }
            if (configuration.containsKey("apiKey")) {
                apiKey = configuration.getProperty("apiKey");
            }
            if (configuration.containsKey("age")) {
                age = Integer.parseInt(configuration.getProperty("age"));
            }
            if (configuration.containsKey("timeframe")) {
                timeframe = configuration.getProperty("timeframe");
            }
            if (configuration.containsKey("keywordSearchParams")) {
                keywordSearchParams = configuration.getProperty("keywordSearchParams").split(",");
            }
            if (configuration.containsKey("exportHistory")) {
                exportHistory = Boolean.parseBoolean(configuration.getProperty("exportHistory"));
            }
            if (configuration.containsKey("plotTimeframe")) {
                plotTimeframe = configuration.getProperty("plotTimeframe");
            }
            if (configuration.containsKey("exportPlot")) {
                exportPlot = Boolean.parseBoolean(configuration.getProperty("exportPlot"));
            }
            if (configuration.containsKey("viewPlot")) {
                viewPlot = Boolean.parseBoolean(configuration.getProperty("viewPlot"));
            }
            if (configuration.containsKey("viewMap")) {
                viewMap = Boolean.parseBoolean(configuration.getProperty("viewMap"));
            }
            return true;
        }
    }


}
