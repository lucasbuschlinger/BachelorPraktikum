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
package de.opendiabetes.vault.plugin.importer.validator;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Validator for Medtronic data.
 *
 * @author Jens Heuschkel
 */
public class MedtronicCSVValidator extends CSVValidator {

    /**
     * Field of the German Medtronic CSV headers containing the date.
     */
    public static final String CARELINK_HEADER_DE_DATE = "Datum";
    /**
     * Field of the German Medtronic CSV headers containing the time.
     */
    public static final String CARELINK_HEADER_DE_TIME = "Zeit";
    /**
     * Field of the German Medtronic CSV headers containing the timestamp.
     */
    public static final String CARELINK_HEADER_DE_TIMESTAMP = "Zeitstempel";
    /**
     * Field of the German Medtronic CSV headers containing the type.
     */
    public static final String CARELINK_HEADER_DE_TYPE = "Roh-Typ";
    /**
     * Field of the German Medtronic CSV headers containing the values.
     */
    public static final String CARELINK_HEADER_DE_VALUE = "Roh-Werte";
    /**
     * Field of the German Medtronic CSV headers containing the sequential number.
     */
    public static final String CARELINK_HEADER_DE_SEQ_NUM = "Roh-Seq Num";
    /**
     * The time format used in Medtronic CSV data.
     */
    public static final String TIME_FORMAT_DE = "dd.MM.yy HH:mm:ss";

    /**
     * The composed header used in Medtronic CSV data.
     */
    public static final String[] CARELINK_HEADER_DE = {
            CARELINK_HEADER_DE_DATE, CARELINK_HEADER_DE_TIME,
            CARELINK_HEADER_DE_TIMESTAMP, CARELINK_HEADER_DE_TYPE,
            CARELINK_HEADER_DE_VALUE
    };

    /**
     * Constructor.
     */
    public MedtronicCSVValidator() {
        //TODO add english header
        super(CARELINK_HEADER_DE, CARELINK_HEADER_DE);
    }

    /**
     * Getter for the raw values.
     *
     * @param creader The CSV reader to use.
     * @return The raw values.
     * @throws IOException Thrown when reading the data goes wrong.
     * @throws UnsupportedOperationException Thrown if English header is selected.
     */
    public String getRawValues(final CsvReader creader) throws IOException, UnsupportedOperationException {
        switch (languageSelection) {
            case DE:
                return creader.get(CARELINK_HEADER_DE_VALUE);
            case EN:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Getter for the sequential number.
     *
     * @param creader The CSV reader to use.
     * @return The raw sequential number.
     * @throws IOException Thrown when reading the data goes wrong.
     * @throws UnsupportedOperationException Thrown if English header is selected.
     */
    public String getRawSeqNum(final CsvReader creader) throws IOException, UnsupportedOperationException {
        switch (languageSelection) {
            case DE:
                return creader.get(CARELINK_HEADER_DE_SEQ_NUM);
            case EN:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Getter for the type.
     *
     * @param creader The CSV reader to use.
     * @return The type.
     * @throws IOException Thrown when reading the data goes wrong.
     * @throws UnsupportedOperationException Thrown if English header is selected.
     */
    public String getCarelinkTypeString(final CsvReader creader) throws IOException, UnsupportedOperationException {
        switch (languageSelection) {
            case DE:
                return creader.get(CARELINK_HEADER_DE_TYPE).trim();
            case EN:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Getter for the type.
     *
     * @param creader The CSV reader to use.
     * @return The type.
     * @throws IOException Thrown when reading the data goes wrong.
     */
    public TYPE getCarelinkType(final CsvReader creader) throws IOException {
        return TYPE.fromString(getCarelinkTypeString(creader));
    }

    /**
     * Getter for the timestamp.
     *
     * @param creader The CSV reader to use.
     * @return The timestamp.
     * @throws IOException Thrown when reading the data goes wrong.
     * @throws ParseException Thrown when a {@link TimestampUtils#createCleanTimestamp(String, String)} can not be created.
     * @throws UnsupportedOperationException Thrown if English header is selected.
     */
    public Date getTimestamp(final CsvReader creader) throws IOException, ParseException, UnsupportedOperationException {
        switch (languageSelection) {
            case DE:
                String timeString = creader.get(CARELINK_HEADER_DE_TIMESTAMP).trim();
                return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_DE);
            case EN:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }


    /**
     * Getter for the timestamp.
     *
     * @param creader The CSV reader to use.
     * @return The timestamp.
     * @throws IOException Thrown when reading the data goes wrong.
     * @throws ParseException Thrown when a {@link TimestampUtils#createCleanTimestamp(String, String)} can not be created.
     * @throws UnsupportedOperationException Thrown if English header is selected.
     */
    public Date getManualTimestamp(final CsvReader creader) throws IOException, ParseException, UnsupportedOperationException {
        switch (languageSelection) {
            case DE:
                String timeString1 = creader.get(CARELINK_HEADER_DE_DATE).trim();
                String timeString2 = creader.get(CARELINK_HEADER_DE_TIME).trim();
                return TimestampUtils.createCleanTimestamp(
                        timeString1 + " " + timeString2, TIME_FORMAT_DE);
            case EN:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * The available types of events for Medtronic data.
     */
    public enum TYPE {
        /**
         * Rewind event.
         */
        REWIND("Rewind"),
        /**
         * Prime event.
         */
        PRIME("Prime"),
        /**
         * Exercise event.
         */
        EXERCICE("JournalEntryExerciseMarker"),
        /**
         * Blood glucose captured on pump event.
         */
        BG_CAPTURED_ON_PUMP("BGCapturedOnPump"),
        /**
         * Blood glucose received event.
         */
        BG_RECEIVED("BGReceived"),
        /**
         * Sensor calibration for blood glucose.
         */
        SENSOR_CAL_BG("SensorCalBG"),
        /**
         * Factor for sensor calibration.
         */
        SENSOR_CAL_FACTOR("SensorCalFactor"),
        /**
         * Glucose sensor data.
         */
        SENSOR_VALUE("GlucoseSensorData"),
        /**
         * Sensor alarm.
         */
        SENSOR_ALERT("AlarmSensor"),
        /**
         * Estimated bolus by wizard.
         */
        BOLUS_WIZARD("BolusWizardBolusEstimate"),
        /**
         * Regular bolus.
         */
        BOLUS_NORMAL("BolusNormal"),
        /**
         * Squared bolus.
         */
        BOLUS_SQUARE("BolusSquare"),
        /**
         * Basal profile.
         */
        BASAL("BasalProfileStart"),
        /**
         * Change of temporal basal percentage.
         */
        BASAL_TMP_PERCENT("ChangeTempBasalPercent"),
        /**
         * Change of temporal basal.
         */
        BASAL_TMP_RATE("ChangeTempBasal"),
        /**
         * Pump alert.
         */
        PUMP_ALERT("AlarmPump"),
        /**
         * Pump suspension state changed.
         */
        PUMP_SUSPEND_CHANGED("ChangeSuspendState"),
        /**
         * NGP pump alert.
         */
        PUMP_ALERT_NGP("AlarmPumpNGP"),
        /**
         * Changed pump time.
         */
        PUMP_TYME_SYNC("ChangeTime");

        /**
         * Type name.
         */
        private final String name;

        /**
         * Constructor for types.
         *
         * @param name Name of the type.
         */
        TYPE(final String name) {
            this.name = name;

        }

        /**
         * Gets the corresponding type from the enum.
         *
         * @param typeString The type to get.
         * @return The corresponding type.
         */
        static TYPE fromString(final String typeString) {
            if (typeString != null && !typeString.isEmpty()) {
                for (TYPE item : TYPE.values()) {
                    if (item.name.equalsIgnoreCase(typeString)) {
                        return item;
                    }
                }
            }
            return null;
        }
    }

}
