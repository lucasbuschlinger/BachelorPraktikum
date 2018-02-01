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


import java.util.List;

/**
 * This class holds the objects used in MiBands JSON files.
 * These objects are represented as classes, to see why we do it this way: take a look into GSON.
 *
 * @author Lucas Buschlinger
 */
public class MiBandObjects {

    /**
     * This object will hold the data if there was some of the {@link HeartMonitorData} monitor type.
     */
    public List<HeartMonitorData> HeartMonitorData;
    /**
     * This object will hold the data if there was some of the {@link SleepIntervalData} interval type.
     */
    public List<SleepIntervalData> SleepIntervalData;
    /**
     * This object will hold the data if there was some of the {@link Workout} type.
     */
    public List<Workout> Workout;
    /**
     * This object will hold the data if there was some of the {@link StepsData} type.
     */
    public List<StepsData> StepsData;
    /**
     * This object will hold the data if there was some of the {@link Weight} type.
     */
    public List<Weight> Weight;

    /*
     * The following are the actual data structures used in the MiBand data.
     * If further fields from these structures are needed one only has to implement accessors to it.
     */

    /**
     * This class resembles the data structure within MiBand heart rate logs.
     */
    public class HeartMonitorData {
        /**
         * The ID of the entry.
         */
        private String rush_id;
        /**
         * The version.
         */
        private int rush_version;
        /**
         * Boolean to indicate whether the entry is hidden.
         */
        private String hidden;
        /**
         * The heart rate.
         */
        private String intensity;
        /**
         * Indicator whether this heart rate has been recorded during an activity.
         */
        private String isActivityValue;
        /**
         * Indicatior whether this heart rate has been recorded during a workout.
         */
        private String isWorkout;
        /**
         * Timestamp when the data has been synced with GoogleFit.
         */
        private String syncedGFit;
        /**
         * The timestamp of the recorded heart rate.
         */
        private String timestamp;

        /**
         * This shows whether the entry is hidden.
         *
         * @return True if the entry is hidden, false otherwise.
         */
        public boolean isHidden() {
            return Boolean.parseBoolean(hidden);
        }

        /**
         * Getter for the heart rate held in {@link #intensity}.
         *
         * @return The heart rate parsed from String.
         */
        public double getHeartRate() {
            return Double.parseDouble(intensity);
        }

        /**
         * Getter for the {@link #timestamp}.
         *
         * @return The timestamp parsed from String.
         */
        public long getTimestamp() {
            return Long.parseLong(timestamp);
        }
    }

    /**
     * This class resembles the data structured within MiBand sleep logs.
     */
    public class SleepIntervalData {
        /**
         * The ID of the entry.
         */
        private String rush_id;
        /**
         * The version.
         */
        private int rush_version;
        /**
         * The ending time of the sleep interval.
         */
        private String endDateTime;
        /**
         * The average heart rate in the sleep interval.
         */
        private String heartRateAvg;
        /**
         * The starting time of the sleep interval.
         */
        private String startDateTime;
        /**
         * The type of the sleep interval.
         */
        private String type;

        /**
         * Getter for the timestamp, which is hereby defined by the end time of the entry.
         *
         * @return The timestamp of the entry.
         */
        public long getTimestamp() {
            return Long.parseLong(startDateTime);
        }

        /**
         * Getter for the average heart rate held in {@link #heartRateAvg}.
         *
         * @return The average heart rate parsed from String.
         */
        public String getHeartRateAvg() {
            return heartRateAvg;
        }

        /**
         * Getter for the duration of the sleep interval.
         * Computed by the difference between start end end times of the entry.
         *
         * @return The duration of the sleep interval.
         */
        public double getDuration() {
            final double millisecPerSec = 1000;
            double start = Double.parseDouble(startDateTime) / millisecPerSec;
            double end = Double.parseDouble(endDateTime) / millisecPerSec;
            return end - start;
        }

        /**
         * Getter for the {@link #type} of sleep within the sleep interval.
         *
         * @return The type of sleep parsed from String.
         */
        public int getType() {
            return Integer.parseInt(type);
        }
    }

    /**
     * This class resembles the data structure within MiBand workout logs.
     */
    public class Workout {
        /**
         * The ID of the entry.
         */
        private String rush_id;
        /**
         * The version.
         */
        private int rush_version;
        /**
         * The ending time of the workout.
         */
        private String endDateTime;
        /**
         * The average heart rate during the workout.
         */
        private String heartAvg;
        /**
         * The duration of the workout in minutes.
         */
        private String minutes;
        /**
         * The starting time of the workout.
         */
        private String startDateTime;
        /**
         * The amount of steps walked during the workout.
         */
        private String steps;
        /**
         * The type of workout.
         */
        private String type;
        /**
         * The amount of calories burned during the workout.
         */
        private String xCalories;
        /**
         * The distance covered during the workout.
         */
        private String xDistance;
        /**
         * The amount of pause made during the workout in minutes.
         */
        private String xMinutesPause;

        /**
         * Getter for the timestamp, which is hereby defined by the end time of the entry.
         *
         * @return The timestamp of the entry.
         */
        public long getTimestamp() {
            return Long.parseLong(startDateTime);
        }

        /**
         * Getter for the duration of the entry in seconds.
         * Computed by the difference between start and end times of the entry.
         * As the timestamps are given in UNIX timestamps, the difference between them is in milliseconds.
         * This gets converted to seconds herein.
         *
         * @return The duration of the workout entry.
         */
        public double getDuration() {
            final double millisecPerSec = 1000;
            double start = Double.parseDouble(startDateTime) / millisecPerSec;
            double end = Double.parseDouble(endDateTime) / millisecPerSec;
            return end - start;
        }

        /**
         * Getter for the average heart rate held in {@link #heartAvg}.
         *
         * @return The average heart rate in the entry.
         */
        public String getHeartAvg() {
            return heartAvg;
        }
    }

    /**
     * This class resembles the data structure within MiBand step logs.
     */
    public class StepsData {

        /**
         * The ID of the entry.
         */
        private String rush_id;
        /**
         * The version.
         */
        private int rush_version;
        /**
         * The timestamp of the data.
         */
        private String dateTime;
        /**
         * Indicates whether the entry is supposed to be hidden.
         */
        private String hidden;
        /**
         * Indicates whether the entry is the last for the day.
         */
        private String last;
        /**
         * Number of steps recorded in this entry.
         */
        private String steps;
        /**
         * Timestamp of when data has been synced with GoogleFit.
         * Is 0 if it has not been synced.
         */
        private String syncedGFit;

        /**
         * Getter for the timestamp.
         * Converts the String to a Long.
         *
         * @return The timestamp.
         */
        public long getTimestamp() {
            return Long.parseLong(dateTime);
        }

        /**
         * Getter for the steps.
         * Converts the String to a Double.
         *
         * @return The number of steps recorded in this entry.
         */
        public int getSteps() {
            return Integer.parseInt(steps);
        }
    }

    /**
     * This class resembles the data structure within MiBand weight logs.
     */
    public class Weight {

        /**
         * The ID of the entry.
         */
        private String rush_id;
        /**
         * The version.
         */
        private int rush_version;
        /**
         * A note for the entry.
         */
        private String note;
        /**
         * Timestamp of when data has been synced with GoogleFit.
         * Is 0 if it has not been synced.
         */
        private String syncedGFit;
        /**
         * The timestamp of the entry.
         */
        private String timestamp;
        /**
         * The value of the entry.
         */
        private String value;

        /**
         * Getter for the timestamp.
         * Converts the String to a Long.
         *
         * @return The timestamp.
         */
        public long getTimestamp() {
            return Long.parseLong(timestamp);
        }

        /**
         * Getter for the weight.
         * Converts the String to a Double.
         *
         * @return The number of steps recorded in this entry.
         */
        public double getWeight() {
            return Double.parseDouble(value);
        }
    }
}
