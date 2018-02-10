/*
 * Copyright (C) 2017 Jens Heuschkel
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
package de.opendiabetes.vault.plugin.interpreter.exerciseInterpreter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.interpreter.VaultInterpreter;
import de.opendiabetes.vault.plugin.util.EasyFormatter;
import de.opendiabetes.vault.plugin.util.SortVaultEntryByDate;
import de.opendiabetes.vault.plugin.util.TimestampUtils;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Wrapper class for the ExerciseInterpreter plugin.
 *
 * @author Magnus Gärtner
 */
public class ExerciseInterpreter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public ExerciseInterpreter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the ExerciseInterpreter plugin.
     */
    @Extension
    public static class ExerciseInterpreterImplementation extends VaultInterpreter {
        /**
         * Conversion factor from minutes to milliseconds.
         */
        private static final int MS_PER_MINUTE = 60000;

        /**
         *
         */
        private int activityThreshold;

        /**
         *
         */
        private int activitySliceThreshold;


        /**
         * {@inheritDoc}
         */
        @Override
        public List<VaultEntry> interpret(final List<VaultEntry> input) {
            List<VaultEntry> data = input;

            // sort by date
            Collections.sort(data, new SortVaultEntryByDate());

            LOG.finer("Start activity data interpretation");
            data = filterActititys(data);
            data = interpretStress(data);

            LOG.finer("Tracker data interpretation finished");

            return data;
        }

        /**
         * //TODO javadoc
         *
         * @param data
         * @return
         */
        private List<VaultEntry> filterActititys(final List<VaultEntry> data) {
            if (data == null || data.isEmpty()) {
                return data;
            }
            List<VaultEntry> retVal = new ArrayList<>();
            List<VaultEntry> dbValues = getDb().queryExerciseBetween(
                    data.get(0).getTimestamp(),
                    data.get(data.size() - 1).getTimestamp());
            VaultEntry lastExerciseItem = null;

            List<VaultEntryAnnotation> annotations = new ArrayList<>();

            for (VaultEntry item : data) {
                switch (item.getType()) {
                    case EXERCISE_BICYCLE:
                    case EXERCISE_RUN:
                    case EXERCISE_WALK:
                    case EXERCISE_OTHER:
                        // google activity type > tracker activity type
                        if (lastExerciseItem == null) {
                            // init item
                            lastExerciseItem = item;
                            // init annotations
                            annotations.addAll(item.getAnnotations());
                        } else if (Math.round(
                                (float) (item.getTimestamp().getTime()
                                        - lastExerciseItem.getTimestamp().getTime())
                                        / MS_PER_MINUTE)
                                >= Math.round(activitySliceThreshold
                                + lastExerciseItem.getValue())) {
                            // within a slice
                            lastExerciseItem.setValue(item.getValue() + lastExerciseItem.getValue()); // add time to value

                            // update annotations
                            annotations = mergeAnnotations(annotations, item.getAnnotations());
                        } else {
                            // new slice --> precess current slice and reset

                            // check db for existing entries
                            for (VaultEntry historyEntry : dbValues) {
                                if (isHistoricElementWithinSlice(lastExerciseItem, historyEntry)) { // is within duration
                                    // merge entries
                                    //db.removeEntry(historyEntry); // TODO move this function in parent class, return hashmap with matchin historic elements

                                    // check if db activity is longer
                                    if (TimestampUtils.addMinutesToTimestamp(
                                            historyEntry.getTimestamp(),
                                            Math.round(historyEntry.getValue())).after(
                                            TimestampUtils.addMinutesToTimestamp(
                                                    lastExerciseItem.getTimestamp(),
                                                    Math.round(lastExerciseItem.getValue())))) {
                                        // extend exercise time
                                        long durationInMilli = TimestampUtils.addMinutesToTimestamp(
                                                historyEntry.getTimestamp().getTime(),
                                                Math.round(historyEntry.getValue()))
                                                - TimestampUtils.addMinutesToTimestamp(
                                                lastExerciseItem.getTimestamp().getTime(),
                                                Math.round(lastExerciseItem.getValue()));
                                        lastExerciseItem.setValue(lastExerciseItem.getValue()
                                                + Math.round(((float) durationInMilli / MS_PER_MINUTE)));
                                    }

                                    annotations = mergeAnnotations(annotations, historyEntry.getAnnotations());
                                } else if (historyEntry.getTimestamp().after(lastExerciseItem.getTimestamp())
                                        && Math.round((float) historyEntry.getTimestamp().getTime() / MS_PER_MINUTE)
                                        > Math.round(lastExerciseItem.getValue())) {
                                    // we passed the current time point --> stop searching
                                    break;
                                }
                            }
                            // save old slice
                            if (lastExerciseItem.getValue() > activityThreshold) {
                                // define type
                                double walk = 0.0;
                                double run = 0.0;
                                double bike = 0.0;
                                double other = 0.0;
                                for (VaultEntryAnnotation annotation : annotations) {
                                    if (!annotation.getValue().isEmpty()) {
                                        try {
                                            switch (annotation.getType()) {
                                                case EXERCISE_GoogleBicycle:
                                                case EXERCISE_TrackerBicycle:
                                                    bike += Double.parseDouble(annotation.getValue());
                                                    break;
                                                case EXERCISE_GoogleRun:
                                                case EXERCISE_TrackerRun:
                                                    run += Double.parseDouble(annotation.getValue());
                                                    break;
                                                case EXERCISE_GoogleWalk:
                                                case EXERCISE_TrackerWalk:
                                                    walk += Double.parseDouble(annotation.getValue());
                                                    break;
                                                case EXERCISE_AUTOMATIC_OTHER:
                                                    other += Double.parseDouble(annotation.getValue());
                                                    break;
                                                default:
                                                    LOG.severe("ASSERTION ERROR in exercice type estimation");
                                                    throw new AssertionError("Case not allowed here !!");
                                            }
                                        } catch (NumberFormatException ex) {
                                            LOG.log(Level.SEVERE, "Error parsing generated double while exercise type estimation", ex);
                                        }
                                    }
                                }
                                // get type with biggest share
                                if (bike > run && bike > walk && bike > other) {
                                    lastExerciseItem.setType(VaultEntryType.EXERCISE_BICYCLE);
                                } else if (run > bike && run > walk && run > other) {
                                    lastExerciseItem.setType(VaultEntryType.EXERCISE_RUN);
                                } else if (walk > bike && walk > run && walk > other) {
                                    lastExerciseItem.setType(VaultEntryType.EXERCISE_WALK);
                                } else {
                                    lastExerciseItem.setType(VaultEntryType.EXERCISE_OTHER);
                                }
                                retVal.add(lastExerciseItem);
                            }

                            //  setup for new slice search
                            lastExerciseItem = item;
                            annotations = new ArrayList<>();
                            annotations.addAll(item.getAnnotations());

                        }
                        break;
                    case BOLUS_NORMAL:
                        break;
                    case BOLUS_SQUARE:
                        break;
                    case BASAL_PROFILE:
                        break;
                    case BASAL_MANUAL:
                        break;
                    case BASAL_INTERPRETER:
                        break;
                    case EXERCISE_MANUAL:
                        // add manual entrys without interpretation
                        retVal.add(item);
                        break;
                    case GLUCOSE_CGM:
                        break;
                    case GLUCOSE_CGM_RAW:
                        break;
                    case GLUCOSE_CGM_ALERT:
                        break;
                    case GLUCOSE_CGM_CALIBRATION:
                        break;
                    case GLUCOSE_BG:
                        break;
                    case GLUCOSE_BG_MANUAL:
                        break;
                    case GLUCOSE_BOLUS_CALCULATION:
                        break;
                    case GLUCOSE_ELEVATION_30:
                        break;
                    case CGM_SENSOR_FINISHED:
                        break;
                    case CGM_SENSOR_START:
                        break;
                    case CGM_CONNECTION_ERROR:
                        break;
                    case CGM_CALIBRATION_ERROR:
                        break;
                    case CGM_TIME_SYNC:
                        break;
                    case MEAL_BOLUS_CALCULATOR:
                        break;
                    case MEAL_MANUAL:
                        break;
                    case MEAL_DESCRIPTION:
                        break;
                    case PUMP_REWIND:
                        break;
                    case PUMP_PRIME:
                        break;
                    case PUMP_FILL:
                        break;
                    case PUMP_FILL_INTERPRETER:
                        break;
                    case PUMP_NO_DELIVERY:
                        break;
                    case PUMP_SUSPEND:
                        break;
                    case PUMP_UNSUSPEND:
                        break;
                    case PUMP_UNTRACKED_ERROR:
                        break;
                    case PUMP_RESERVOIR_EMPTY:
                        break;
                    case PUMP_TIME_SYNC:
                        break;
                    case PUMP_AUTONOMOUS_SUSPEND:
                        break;
                    case PUMP_CGM_PREDICTION:
                        break;
                    case SLEEP_LIGHT:
                        break;
                    case SLEEP_REM:
                        break;
                    case SLEEP_DEEP:
                        break;
                    case HEART_RATE:
                        break;
                    case HEART_RATE_VARIABILITY:
                        break;
                    case STRESS:
                        break;
                    case KETONES_BLOOD:
                        break;
                    case KETONES_URINE:
                        break;
                    case KETONES_MANUAL:
                        break;
                    case LOC_TRANSITION:
                        break;
                    case LOC_HOME:
                        break;
                    case LOC_WORK:
                        break;
                    case LOC_FOOD:
                        break;
                    case LOC_SPORTS:
                        break;
                    case LOC_OTHER:
                        break;
                    case BLOOD_PRESSURE:
                        break;
                    case ML_CGM_PREDICTION:
                        break;
                    case DM_INSULIN_SENSITIVITY:
                        break;
                    case OTHER_ANNOTATION:
                        break;
                    case Tag:
                        break;
                    default:
                        retVal.add(item);
                }
            }

            // add last unsliced item
            if (lastExerciseItem != null
                    && lastExerciseItem.getValue() > activityThreshold) {
                retVal.add(lastExerciseItem);
            }

            return retVal;
        }

        /**
         * //TODO javadoc
         *
         * @param currentAnnotations
         * @param additionalAnnotations
         * @return
         */
        private List<VaultEntryAnnotation> mergeAnnotations(final List<VaultEntryAnnotation> currentAnnotations,
                                                            final List<VaultEntryAnnotation> additionalAnnotations) {
            if (currentAnnotations == null || additionalAnnotations == null) {
                return currentAnnotations;
            }
            for (VaultEntryAnnotation itemAnnoation : additionalAnnotations) {
                boolean merged = false;
                for (VaultEntryAnnotation seenAnnotaion : currentAnnotations) {
                    if (itemAnnoation.toString().equalsIgnoreCase(seenAnnotaion.toString())) {
                        try {
                            if (!itemAnnoation.getValue().isEmpty()
                                    && !seenAnnotaion.getValue().isEmpty()) { // merge two values
                                seenAnnotaion.setValue(EasyFormatter.formatDouble(
                                        Double.parseDouble(seenAnnotaion.getValue())
                                                + Double.parseDouble(itemAnnoation.getValue())));
                            } else if (!itemAnnoation.getValue().isEmpty()
                                    && seenAnnotaion.getValue().isEmpty()) { // add value
                                seenAnnotaion.setValue(itemAnnoation.getValue());
                            }

                        } catch (NumberFormatException ex) {
                            LOG.log(Level.SEVERE, "Error parsing generated double while exercise annotation merging", ex);
                        }
                        merged = true;
                        break;
                    }
                }
                if (!merged) {
                    currentAnnotations.add(itemAnnoation);
                }
            }
            return currentAnnotations;
        }

        /**
         * //TODO javadoc
         *
         * @param item
         * @param historyElement
         * @return
         */
        private boolean isHistoricElementWithinSlice(final VaultEntry item, final VaultEntry historyElement) {
            return item != null && historyElement != null // not null
                    && TimestampUtils.addMinutesToTimestamp(historyElement.getTimestamp(), -1 * activitySliceThreshold).after(item.getTimestamp()) // starts after current item (with respect to slice threshold)
                    && TimestampUtils.addMinutesToTimestamp(item.getTimestamp(), Math.round(item.getValue() + activitySliceThreshold)).after(historyElement.getTimestamp()); // starts befor item ends (with respect to slice threshold)
        }

        /**
         * //TODO javadoc
         *
         * @param data
         * @return
         */
        private List<VaultEntry> interpretStress(final List<VaultEntry> data) {
            List<VaultEntry> addData = new ArrayList<>();
            VaultEntry lastItem = null;
            for (VaultEntry item : data) {
                if (item.getType() == VaultEntryType.STRESS) {
                    if (lastItem == null) {
                        //warm up
                        lastItem = item;
                    } else {
                        if (item.getTimestamp().getTime()
                                - lastItem.getTimestamp().getTime() > 6300000) { // > 10.5 min //TODO ask jens should be 630000
                            // add entry with 0, 10 minutes after last timestamp
                            addData.add(new VaultEntry(VaultEntryType.STRESS,
                                    TimestampUtils.addMinutesToTimestamp(
                                            lastItem.getTimestamp(), 10), 0));
                        }

                        lastItem = item;
                    }

                }
            }

            // check last item again
            if (lastItem != null
                    && data.get(data.size() - 1).getTimestamp().getTime()
                    - lastItem.getTimestamp().getTime() > 6300000) { // > 10.5 min
                // add entry with 0
                addData.add(new VaultEntry(VaultEntryType.STRESS,
                        TimestampUtils.addMinutesToTimestamp(
                                lastItem.getTimestamp(), 10), 0));
            }

            data.addAll(addData);
            Collections.sort(data, new SortVaultEntryByDate());

            return data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            if (!configuration.containsKey("activityThreshold") || !configuration.containsKey("activitySliceThreshold")) {
                return false;
            }

            activityThreshold = Integer.parseInt(configuration.getProperty("activityThreshold"));
            activitySliceThreshold = Integer.parseInt(configuration.getProperty("activitySliceThreshold"));
            return true;
        }
    }
}