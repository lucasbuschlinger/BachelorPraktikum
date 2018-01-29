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
package de.opendiabetes.vault.plugin.importer.miband;

import com.google.gson.Gson;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.FileImporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Wrapper class for the MiBandNotifyImporter plugin.
 *
 * @author Lucas Buschlinger
 */
public class MiBandNotifyImporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link PluginWrapper}.
     */
    public MiBandNotifyImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the MiBandNotify importer plugin.
     */
    @Extension
    public static class MiBandNotifyImporterImplementation extends FileImporter {

        private final int defaultHeartRateLowerBound = 40;
        private final int defaultHeartRateUpperBound = 250;
        private final int defaultExerciseHeartThresholdMid = 90;
        private final int defaultExerciseHeartThresholdHigh = 130;
        private final int defaultMaxTimeGapSeconds = 60;

        private final int statusLoadedConfig = 25;
        private final int statusReadJSON = 50;
        private final int statusImportedEntries = 75;
        private final int statusInterpretedEntries = 100;


        private int heartRateLowerBound;
        private int heartRateUpperBound;
        private int exerciseHeartThresholdMid;
        private int exerciseHeartThresholdHigh;
        private int maxTimeGapSeconds;

        /**
         * Constructor.
         * This also sets the default values for the thresholds between different kinds of exercises and heart rate bounds.
         */
        public MiBandNotifyImporterImplementation() {
            this.heartRateLowerBound = defaultHeartRateLowerBound;
            this.heartRateUpperBound = defaultHeartRateUpperBound;
            this.exerciseHeartThresholdMid = defaultExerciseHeartThresholdMid;
            this.exerciseHeartThresholdHigh = defaultExerciseHeartThresholdHigh;
            this.maxTimeGapSeconds = defaultMaxTimeGapSeconds;
        }

        /**
         * Preprocessing not needed.
         */
        @Override
        protected void preprocessingIfNeeded(final String filePath) { }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean processImport(final InputStream fileInputStream, final String filenameForLogging) {
            importedData = new ArrayList<>();

           Gson gson = new Gson();

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            } catch (UnsupportedEncodingException exception) {
                System.out.println("Can not handle fileInputStream, unsupported encoding (non UTF-8)!");
                return false;
            }

            // Reading the JSON file
            MiBandObjects data = gson.fromJson(reader, MiBandObjects.class);
            this.notifyStatus(statusReadJSON, "Read JSON file.");
            if (data.SleepIntervalData == null && data.HeartMonitorData == null && data.Workout == null) {
                LOG.log(Level.SEVERE, "Got no data from JSON import!");
                return false;
            }

            // Seeing, whether the data contained heart rate related data
            if (data.HeartMonitorData != null) {
                importedData.addAll(processHeartData(data));
            }
            if (data.SleepIntervalData != null) {
                importedData.addAll(processSleepData(data));
            }
            if (data.Workout != null) {
                importedData.addAll(processWorkoutData(data));
            }
            this.notifyStatus(statusImportedEntries, "Successfully imported MiBand data to VaultEntries");
            importedData = interpretMiBand(importedData);
            this.notifyStatus(statusInterpretedEntries, "Interpreted entries and closed gaps");
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            if (configuration.containsKey("heartRateLowerBound")) {
                heartRateLowerBound = Integer.parseInt(configuration.getProperty("heartRateLowerBound"));
            }
            if (configuration.containsKey("heartRatUpperBound")) {
                heartRateUpperBound = Integer.parseInt(configuration.getProperty("heartRateUpperBound"));
            }
            if (configuration.containsKey("exerciseHeartThresholdMid")) {
                exerciseHeartThresholdMid = Integer.parseInt(configuration.getProperty("exerciseHeartThresholdMid"));
            }
            if (configuration.containsKey("exerciseHeartThresholdHigh")) {
                exerciseHeartThresholdHigh = Integer.parseInt(configuration.getProperty("exerciseHeartThresholdHigh"));
            }
            if (configuration.containsKey("maxTimeGap")) {
                maxTimeGapSeconds = Integer.parseInt(configuration.getProperty("maxTimeGap"));
            }
            LOG.log(Level.INFO, "Successfully loaded configuration.");
            this.notifyStatus(statusLoadedConfig, "Loaded configuration from properties file");
            return true;
        }

        /**
         * This method is used to convert the JSON/GSON object to {@link VaultEntry}.
         * In particular this converts heart rate data.
         *
         * @param data The GSON/JSON data.
         * @return The data as {@link VaultEntry}.
         */
        private List<VaultEntry> processHeartData(final MiBandObjects data) {
            List<VaultEntry> entries = new ArrayList<>();
            for (MiBandObjects.HeartMonitorData item : data.HeartMonitorData) {
                if (!item.isHidden()) {
                    double heartRate = item.getHeartRate();
                    Date timestamp = new Date(item.getTimestamp());
                    if (heartRate > heartRateLowerBound && heartRate < heartRateUpperBound) {
                        VaultEntry entry = new VaultEntry(VaultEntryType.HEART_RATE, timestamp, heartRate);
                        entries.add(entry);
                    } else {
                        LOG.log(Level.INFO, String.format("Abnormal heart rate (%.1f BPM) while exercising, skipping entry.", heartRate));
                    }
                }
            }
            return entries;
        }

        /**
         * This method is used to convert the JSON/GSON object to {@link VaultEntry}.
         * In particular this converts sleeping data.
         *
         * @param data The GSON/JSON data.
         * @return The data as {@link VaultEntry}.
         */
        private List<VaultEntry> processSleepData(final MiBandObjects data) {
            List<VaultEntry> entries = new ArrayList<>();
            final int deepSleep = 5;
            final int lightSleep = 4;
            for (MiBandObjects.SleepIntervalData item : data.SleepIntervalData) {
                int typeInt = item.getType();
                Date timestamp = new Date(item.getTimestamp());
                List<VaultEntryAnnotation> annotation = new ArrayList<>();
                annotation.add(new VaultEntryAnnotation(item.getHeartRateAvg(), VaultEntryAnnotation.TYPE.AVERAGE_HEART_RATE));
                double duration = item.getDuration();
                VaultEntryType type;
                if (typeInt == lightSleep) {
                    type = VaultEntryType.SLEEP_LIGHT;
                } else if (typeInt == deepSleep) {
                    type = VaultEntryType.SLEEP_DEEP;
                } else {
                    LOG.log(Level.INFO, "Unknown sleep type, skipping entry.");
                    continue;
                }
                entries.add(new VaultEntry(type, timestamp, duration, annotation));
            }
            return entries;
        }

        /**
         * This method is used to convert the JSON/GSON object to {@link VaultEntry}.
         * In particular this converts workout data.
         *
         * @param data The GSON/JSON data.
         * @return The data as {@link VaultEntry}.
         */
        private List<VaultEntry> processWorkoutData(final MiBandObjects data) {
            List<VaultEntry> entries = new ArrayList<>();
            for (MiBandObjects.Workout item : data.Workout) {
                String heartRate = item.getHeartAvg();
                List<VaultEntryAnnotation> annotation = new ArrayList<>();
                annotation.add(new VaultEntryAnnotation(heartRate, VaultEntryAnnotation.TYPE.AVERAGE_HEART_RATE));
                int heartRateValue = Integer.parseInt(heartRate);
                double duration = item.getDuration();
                Date timestamp = new Date(item.getTimestamp());
                VaultEntryType type;
                if (heartRateValue < heartRateLowerBound || heartRateValue > heartRateUpperBound) {
                    LOG.log(Level.INFO, String.format("Abnormal heart rate (%d BPM) while exercising, skipping entry.", heartRateValue));
                    continue;
                }
                if (heartRateValue < exerciseHeartThresholdMid) {
                    type = VaultEntryType.EXERCISE_LOW;
                } else if (heartRateValue < exerciseHeartThresholdHigh) {
                    type = VaultEntryType.EXERCISE_MID;
                } else {
                    type = VaultEntryType.EXERCISE_HIGH;
                }
                entries.add(new VaultEntry(type, timestamp, duration, annotation));
            }
            return entries;
        }

        /**
         * This interprets the MiBand data and fills significant gaps between entries by adding copies with different timestamps.
         * The step that should be present between individual entries should not be larger than specified in {@link #maxTimeGapSeconds}.
         *
         * @param entries The imported entries which will get interpreted.
         * @return The entries with filled gaps.
         */
        private List<VaultEntry> interpretMiBand(final List<VaultEntry> entries) {
            List<VaultEntry> returnList = new ArrayList<>();
            final int msPerSec = 1000;
            int timeStep = maxTimeGapSeconds * msPerSec;
            for (VaultEntry entry : entries) {
                returnList.add(entry);
                // No need to fill gaps with heart rate entries
                if (entry.getType() == VaultEntryType.HEART_RATE) {
                    continue;
                }
                double timespan = entry.getValue();
                if (timespan > maxTimeGapSeconds) {
                    VaultEntryType type = entry.getType();
                    List<VaultEntryAnnotation> annotations = entry.getAnnotations();
                    int newEntries = (int) (timespan / maxTimeGapSeconds);
                    for (int i = 1; i < newEntries; i++) {
                        Date newTimestamp = new Date(entry.getTimestamp().getTime() + i * timeStep);
                        returnList.add(new VaultEntry(type, newTimestamp, timespan, annotations));
                    }
                }
            }
            return returnList;
        }
    }
}
