/**
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

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This class models annotations of vault entries.
 */
public class VaultEntryAnnotation implements Serializable {

    /**
     * The pattern of the VaultEntryAnnotation's value.
     */
    private final Pattern valuePattern;
    /**
     * The type of the VaultEntryAnnotation.
     */
    private TYPE type;
    /**
     * The value of the VaultEntryAnnotation.
     */
    private String value = "";

    /**
     * The no-argument constructor of VaultEntryAnnotation, setting a default annotation type.
     */
    public VaultEntryAnnotation() {
        this("", TYPE.EXERCISE_AUTOMATIC_OTHER);
    }

    /**
     * A constructor of VaultEntryAnnotation, setting the annotation type.
     *
     * @param type The parameter that type will be set to.
     */
    public VaultEntryAnnotation(final TYPE type) {
        this("", type);
    }

    /**
     * A constructor of VaultEntryAnnotation, setting the annotation type and value.
     *
     * @param value The parameter that value will be set to.
     * @param type  The parameter that type will be set to.
     */
    public VaultEntryAnnotation(final String value, final TYPE type) {
        this.value = value;
        this.type = type;
        valuePattern = Pattern.compile(".*" + type.toString() + "(=([\\w\\.]+))?.");
    }

    /**
     * Getter for type.
     *
     * @return The type of the VaultEntryAnnotation.
     */
    public TYPE getType() {
        return type;
    }

    /**
     * Getter for value.
     *
     * @return The value of the VaultEntryAnnotation.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the VaultEntryAnnotation's value.
     *
     * @param value The parameter that value will be set to.
     * @return The same VaultEntryAnnotation with the modified value.
     */
    public VaultEntryAnnotation setValue(final String value) {
        this.value = value;
        return this;
    }

    /**
     * Converting VaultEntries to string.
     * @return the VaultEntry as string.
     * TODO.
     */
    @Override
    public String toString() {
        return type.toString();
    }

    // TODO reimplement with pattern matching
//    /**
//     *
//     * @param annotationString representing VaultEntryAnnotation as string
//     *
//     * @return VaultEntryAnnotation represented by the string or null if no (or
//     * more than one) VaultEntryAnnotation found
//     */
//    public static VaultEntryAnnotation fromString(String annotationString) {
//        VaultEntryAnnotation returnValue = null;
//        for (VaultEntryAnnotation item : VaultEntryAnnotation.values()) {
//            if (annotationString.toUpperCase().contains(item.toString().toUpperCase())) {
//                if (returnValue != null) {
//                    returnValue = item;
//                } else {
//                    // found more than one --> error
//                    return null;
//                }
//            }
//        }
//        return returnValue;
//    }

    /**
     * Converts VaultEntry to string with the value.
     * @return The VaultEntry as string.
     */
    public String toStringWithValue() {
        return value.isEmpty() ? this.toString() : this.toString() + "=" + value;
    }

    /**
     * Getter for a hash code.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        final int hash = 7;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VaultEntryAnnotation other = (VaultEntryAnnotation) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return this.type == other.type;
    }

    /**
     * This enum defines different types of vault entry annotations.
     */
    public enum TYPE {
        /**
         * Indicates lase time glucose level rose.
         */
        GLUCOSE_RISE_LAST,
        /**
         * Indicates glucose rising for 20 minutes.
         */
        GLUCOSE_RISE_20_MIN,
        /**
         * TODO.
         */
        GLUCOSE_BG_METER_SERIAL,
        /**
         * Indicates walking exercise recorded by a tracker.
         */
        EXERCISE_TrackerWalk,
        /**
         * Indicates cycling exercise recorded by a tracker.
         */
        EXERCISE_TrackerBicycle,
        /**
         * Indicates running exercise recorded by a tracker.
         */
        EXERCISE_TrackerRun,
        /**
         * Indicates walking exercise recorded by google fit.
         */
        EXERCISE_GoogleWalk,
        /**
         * Indicates cycling exercise recorded by a google fit.
         */
        EXERCISE_GoogleBicycle,
        /**
         * Indicates running exercise recorded by a google fit.
         */
        EXERCISE_GoogleRun,
        /**
         * Indicates other exercise.
         */
        EXERCISE_AUTOMATIC_OTHER,
        /**
         * The pump error code.
         */
        PUMP_ERROR_CODE,
        /**
         * The pump information code.
         */
        PUMP_INFORMATION_CODE,
        /**
         * Indicates medtronic data.
         */
        CGM_VENDOR_MEDTRONIC,
        /**
         * Indicates libre data.
         */
        CGM_VENDOR_LIBRE,
        /**
         * Indicates dexcom data.
         */
        CGM_VENDOR_DEXCOM,
        /**
         * The prediction time for the machine learner.
         */
        ML_PREDICTION_TIME_BUCKET_SIZE,
        /**
         * User text.
         */
        USER_TEXT,

        BOLUS_CORRECTION,

        BOLUS_MEAL,

        BLOOD_PRESSURE_Systolic,

        BLOOD_PRESSURE_Diastolic,

        EXERCISE_cosy,

        EXERCISE_ordinary,

        EXERCISE_demanding,

        MEAL_Information,

        TAG_BeforeTheMeal,

        TAG_Breakfast,

        TAG_Correction,

        TAG_Sports,

        TAG_Snack,

        TAG_Dinner,

        TAG_OfficeWork,

        TAG_Lunch,

        TAG_Bedtime,

        TAG_HypoFeeling,

        TAG_Sad,

        TAG_Sick,

        TAG_AfterTheMeal,

        TAG_HyperFeeling,

        TAG_Party,

        TAG_Bingeing,

        TAG_Alcohol,

        TAG_Nervous,

        TAG_Stress,

        TAG_Note,

        /**
         * Unhandled Tags get this value
         */
        TAG_Unknown
    }

}
