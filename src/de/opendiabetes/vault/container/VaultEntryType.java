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
package de.opendiabetes.vault.container;

/**
 * This enum defines different vault entry types.
 */
public enum VaultEntryType {
    // Bolus
// Bolus
    /**
     * Regular bolus.
     */
    BOLUS_NORMAL,
    /**
     * Squared bolus.
     */
    BOLUS_SQUARE,
    // Basal
    /**
     * Profile basal value.
     */
    BASAL_PROFILE,
    /**
     * Manual basal value.
     */
    BASAL_MANUAL,
    /**
     * Interpreter basal value.
     */
    BASAL_INTERPRETER,
    // Exercise
    /**
     * Manual exercise.
     */
    EXERCISE_MANUAL,
    /**
     * Other exercise.
     */
    EXERCISE_OTHER,
    /**
     * Walking.
     */
    EXERCISE_WALK,
    /**
     * Cycling.
     */
    EXERCISE_BICYCLE,
    /**
     * Running.
     */
    EXERCISE_RUN,
    // Glucose
    /**
     * Continuous glucose monitoring glucose value.
     */
    GLUCOSE_CGM,
    /**
     * Continuous glucose monitoring raw glucose value.
     */
    GLUCOSE_CGM_RAW,
    /**
     * Continuous glucose monitoring glucose alert.
     */
    GLUCOSE_CGM_ALERT,
    /**
     * Continuous glucose monitoring glucose calibration.
     */
    GLUCOSE_CGM_CALIBRATION,
    /**
     * Blood glucose value.
     */
    GLUCOSE_BG,
    /**
     * Manual blood glucose value.
     */
    GLUCOSE_BG_MANUAL,
    /**
     * Glucose bolus calculation.
     */
    GLUCOSE_BOLUS_CALCULATION,
    /**
     * Elevated glucose.
     */
    GLUCOSE_ELEVATION_30,
    // CGM system
    /**
     * Continuous glucose monitoring sensor finished.
     */
    CGM_SENSOR_FINISHED,
    /**
     * Continuous glucose monitoring sensor starting.
     */
    CGM_SENSOR_START,
    /**
     * Continuous glucose monitoring sensor error.
     */
    CGM_CONNECTION_ERROR,
    /**
     * Continuous glucose monitoring calibration error.
     */
    CGM_CALIBRATION_ERROR,
    /**
     * Continuous glucose monitoring time synchronization.
     */
    CGM_TIME_SYNC,
    // Meal
    /**
     * Meal bolus calculator.
     */
    MEAL_BOLUS_CALCULATOR,
    /**
     * Manual meal.
     */
    MEAL_MANUAL,
    /**
     * Meal description.
     */
    MEAL_DESCRIPTION,
    // Pump Events
    /**
     * Pump rewind event.
     */
    PUMP_REWIND,
    /**
     * Pump prime event.
     */
    PUMP_PRIME,
    /**
     * Pump fill event.
     */
    PUMP_FILL,
    /**
     * Pump fill interpreter.
     */
    PUMP_FILL_INTERPRETER,
    /**
     * No delivery from pump.
     */
    PUMP_NO_DELIVERY,
    /**
     * Pump suspended.
     */
    PUMP_SUSPEND,
    /**
     * Pump unsuspended.
     */
    PUMP_UNSUSPEND,
    /**
     * Untracked pump error.
     */
    PUMP_UNTRACKED_ERROR,
    /**
     * Pump's reservoir empty.
     */
    PUMP_RESERVOIR_EMPTY,
    /**
     * Pump time synchronization.
     */
    PUMP_TIME_SYNC,
    /**
     * Autonomous pump suspension.
     */
    PUMP_AUTONOMOUS_SUSPEND,
    /**
     * Continuous glucose monitoring pump prediction.
     */
    PUMP_CGM_PREDICTION,
    // Sleep
    /**
     * Light sleep.
     */
    SLEEP_LIGHT,
    /**
     * REM sleep.
     */
    SLEEP_REM,
    /**
     * Deep sleep.
     */
    SLEEP_DEEP,
    // Heart
    /**
     * Heart rate value.
     */
    HEART_RATE,
    /**
     * Heart rate variability.
     */
    HEART_RATE_VARIABILITY,
    /**
     * Stress.
     */
    STRESS,
    // Ketones
    /**
     * Blood ketones.
     */
    KETONES_BLOOD,
    /**
     * Urine ketones.
     */
    KETONES_URINE,
    /**
     * Manual ketones.
     */
    KETONES_MANUAL,
    // Location (Geocoding)
    /**
     * Location transition.
     */
    LOC_TRANSITION,
    /**
     * Home location.
     */
    LOC_HOME,
    /**
     * Work location.
     */
    LOC_WORK,
    /**
     * Food location.
     */
    LOC_FOOD,
    /**
     * Sports location.
     */
    LOC_SPORTS,
    /**
     * Other location.
     */
    LOC_OTHER,
    //Blood Pressure
    /**
     * Blood pressure.
     */
    BLOOD_PRESSURE,
    // Machine Learning
    /**
     * Machine learner's continuous glucose monitoring prediction.
     */
    ML_CGM_PREDICTION,
    // Date Mining
    /**
     * Insulin sensitivity for data mining.
     */
    DM_INSULIN_SENSITIVITY,
    // More unspecific input
    /**
     * Other annotations.
     */
    OTHER_ANNOTATION,
    //Tags
    /**
     * Various tags.
     */
    Tag
}
