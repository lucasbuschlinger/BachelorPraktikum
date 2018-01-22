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
package de.opendiabetes.vault.plugin.importer.libretext;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.plugin.importer.validator.CSVValidator;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

/**
 * Implementation of a validator for LibreText based CSV data.
 *
 * @author Lucas Buschlinger
 * @author Magnus Gärtner
 */
public class LibreTextCSVValidator extends CSVValidator {

    /**
     * The header field of LibreText CSV data which contains the scanned glucose values.
     */
    private static final String LIBRE_HEADER_DE_SCAN     = "Gescannte Glukose (mg/dL)";
    /**
     * The header field of LibreText CSV data which contains the historic glucose values.
     */
    private static final String LIBRE_HEADER_DE_HISTORIC =  "Historische Glukose (mg/dL)";
    /**
     * The header field of LibreText CSV data which contains the blood glucose values.
     */
    private static final String LIBRE_HEADER_DE_BLOOD    = "Teststreifen-Blutzucker (mg/dL)";
    /**
     * The header field of LibreText CSV data which contains the timestamp.
     */
    private static final String LIBRE_HEADER_DE_TIME     = "Uhrzeit";
    /**
     * The time format used in LibreText CSV data.
     */
    private static final String TIME_FORMAT_LIBRE_DE    = "yyyy.MM.dd HH:mm";

    /**
     * The composed German header used in LibreText CSV data.
     */
    private static final String[] LIBRE_HEADER_DE = {
            LIBRE_HEADER_DE_SCAN,
            LIBRE_HEADER_DE_HISTORIC,
            LIBRE_HEADER_DE_BLOOD,
            LIBRE_HEADER_DE_TIME,
            TIME_FORMAT_LIBRE_DE
    };


    /**
     * Constructor for a LibreTextCSV validator.
     */
    public LibreTextCSVValidator() {
        super(LIBRE_HEADER_DE, LIBRE_HEADER_DE);
    }

    /**
     * Gets the type of the entry currently getting parsed by reading the appropriate field in the LibreText CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The type of the entry, it is either of {@link TYPE}.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     */
    public TYPE getType(final CsvReader reader) throws IOException {
        int typeInt = Integer.parseInt(reader.get("Art des Eintrags"));
        return TYPE.fromInt(typeInt);
    }

    /**
     * Gets the timestamp of the entry currently getting parsed by
     * reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The timestamp of the entry.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws ParseException Thrown if the String containing the data can not be parsed
     * in {@link TimestampUtils#createCleanTimestamp(String, String)}.
     */
    public Date getTimestamp(final CsvReader reader) throws  IOException, ParseException {
        String timeString = reader.get(LIBRE_HEADER_DE_TIME);
        return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_LIBRE_DE);
    }

    /**
     * Gets the value of the historic glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the historic glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     */
    public double getHistoricGlucose(final CsvReader reader) throws IOException {
        return Double.parseDouble(reader.get(LIBRE_HEADER_DE_HISTORIC));
    }

    /**
     * Gets the value of the scanned glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the scanned glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     */
    public double getScanGlucose(final CsvReader reader) throws IOException {
        return Double.parseDouble(reader.get(LIBRE_HEADER_DE_SCAN));
    }

    /**
     * Gets the value of the blood glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the blood glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     */
    public double getBloodGlucose(final CsvReader reader) throws IOException {
        return Double.parseDouble(reader.get(LIBRE_HEADER_DE_BLOOD));
    }

    /**
     * This enum lists the different types of data present in LibreText CSv data.
     */
    public enum TYPE {
        /**
         * Indicates that the value is that of scanned glucose.
         */
        SCAN_GLUCOSE(1),
        /**
         * Indicates that the value is that of historic glucose.
         */
        HISTORIC_GLUCOSE(0),
        /**
         * Indicates that the value is that of blood glucose.
         */
        BLOOD_GLUCOSE(2),
        /**
         * Indicates that the value is the timestamp.
         */
        TIME_CHANGED(6);

        /**
         * The numeric representation of the types of data.
         */
        private final int index;

        /**
         * Constructor for types used in LibreText CSV data.
         * @param index The numeric representation of the type.
         */
        TYPE(final int index) {
            this.index = index;
        }

        /**
         * Gets the type which is bound to the numeric representation specified in the argument.
         *
         * @param type The numeric representation of the type.
         * @return The type bound to the index.
         */
        static TYPE fromInt(final int type) {
            final int timestamp = 6;
            switch (type) {
                case 1:
                    return SCAN_GLUCOSE;
                case 0:
                    return HISTORIC_GLUCOSE;
                case 2:
                    return BLOOD_GLUCOSE;
                case timestamp:
                    return TIME_CHANGED;
                default:
                    LOG.log(Level.SEVERE, "Error while type checking!");
                    throw new IllegalArgumentException("No such type for LibreText data.");
            }
        }
    }
}
