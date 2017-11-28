/**
 * Copyright (C) 2017 OpenDiabetes
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
package de.opendiabetes.vault.plugin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class implements timestamps.
 */
public class TimestampUtils {


    //public static final String TIME_FORMAT_LIBRE_DE = "yyyy.MM.dd HH:mm"; TODO: deprecated?

    /**
     * Constructor for a clean timestamp.
     * @param dateTime The data and time for the timestamp.
     * @param format The format of the dateTime string.
     * @return The Date
     * @throws ParseException Gets thrown if the dateTime can not be parsed.
     */
    public static Date createCleanTimestamp(String dateTime, String format) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date rawDate = df.parse(dateTime);
        return createCleanTimestamp(rawDate);
    }

    /**
     * Utility to convert a timestamp from Date to String.
     * TODO: deprecated?
     * @param timestamp The timestamp to be converted.
     * @param format The format of the timestamp.
     * @return The timestamp as a string.
     */
    public static String timestampToString(Date timestamp, String format) {
        return new SimpleDateFormat(format).format(timestamp);
    }

    /**
     * Method to create a clean timestamp from a date object.
     * @param rawDate The date to create a timestamp of.
     * @return The cleaned timestamp.
     */
    public static Date createCleanTimestamp(Date rawDate) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(rawDate);
        // round to 5 minutes
        //        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        //        int mod = unroundedMinutes % 5;
        //        calendar.add(Calendar.MINUTE, mod < 3 ? -mod : (5 - mod));
        // round to 1 minute
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * Method to add minutes to a Date timestamp.
     * @param timestamp The timestamp to add the minutes to.
     * @param minutes The minuted to be added.
     * @return The timestamp with minutes.
     */
    public static Date addMinutesToTimestamp(Date timestamp, long minutes) {
        return new Date(addMinutesToTimestamp(timestamp.getTime(), minutes));
    }

    /**
     * Method to add minuted to a Long timestamp.
     * @param timestamp The timestamp to add the minutes to.
     * @param minutes The minutes to be added.
     * @return The timestamp with minutes.
     */
    public static long addMinutesToTimestamp(long timestamp, long minutes) {
        timestamp += minutes * 60000; // 1 m = 60000 ms
        return timestamp;
    }

    /**
     * TODO.
     * @param inputDate
     * @return
     */
    public static Date fromLocalDate(LocalDate inputDate) {
        return fromLocalDate(inputDate, 0);
    }

    /**
     * Method to convert a date to local time.
     * @param inputDate The date to be converted.
     * @return The local time.
     * @link https://blog.progs.be/542/date-to-java-time
     */
    public static LocalTime dateToLocalTime(Date inputDate) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(inputDate.getTime()), ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * TODO.
     * @param inputDate
     * @param offsetInMilliseconds
     * @return
     */
    public static Date fromLocalDate(LocalDate inputDate, long offsetInMilliseconds) {
        Date tmpInputDate = Date.from(Instant.from(inputDate
                .atStartOfDay(ZoneId.systemDefault())));
        if (offsetInMilliseconds > 0) {
            tmpInputDate = new Date(tmpInputDate.getTime() + offsetInMilliseconds);
        }
        return tmpInputDate;
    }

    /**
     * TODO.
     * @param timestamp
     * @return
     */
    public static int getHourOfDay(Date timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * TODO.
     * @param timestamp
     * @return
     */
    public static int getMinuteOfHour(Date timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * Method to check whether a give point in time is within a span of time.
     * @param startTime     Start of the time span.
     * @param endTime       end of the time span.
     * @param timePoint     checks if this time point is within timespan.
     * @return True if the time is within the borders.
     */
    public static boolean withinDateTimeSpan(Date startTime, Date endTime,
                                             Date timePoint) {
        return startTime.before(timePoint) && endTime.after(timePoint)
                || startTime.equals(timePoint) || endTime.equals(timePoint);

    }

    /**
     * Method to check whether a give point in time is within a span of time.
     * @param startTime     Start of the time span.
     * @param endTime       end of the time span.
     * @param timePoint     checks if this time point is within timespan.
     * @return True if the time is within the borders.
     */
    public static boolean withinTimeSpan(LocalTime startTime, LocalTime endTime, LocalTime timePoint) {
        if (startTime.isBefore(endTime)) {
            // timespan is wihtin a day
            return (timePoint.isAfter(startTime) || timePoint.equals(startTime))
                    && (timePoint.isBefore(endTime) || timePoint.equals(endTime));
        } else {
            // timespan is not within a day (through midnight, e.g.  23:30 - 0:15)
            return (timePoint.isAfter(startTime) || timePoint.equals(startTime))
                    || (timePoint.isBefore(endTime) || timePoint.equals(endTime));
        }
    }

    /**
     * Method to check whether a give point in time is within a span of time.
     * @param startTime     Start of the time span.
     * @param endTime       end of the time span.
     * @param timePoint     checks if this time point is within timespan.
     * @return True if the time is within the borders.
     */
    public static boolean withinTimeSpan(LocalTime startTime, LocalTime endTime, Date timePoint) {

        LocalTime tp = dateToLocalTime(timePoint);
        if (startTime.isBefore(endTime)) {
            // timespan is wihtin a day
            return (tp.isAfter(startTime) || tp.equals(startTime))
                    && (tp.isBefore(endTime) || tp.equals(endTime));
        } else {
            // timespan is not within a day (through midnight, e.g.  23:30 - 0:15)
            return (tp.isAfter(startTime) || tp.equals(startTime))
                    || (tp.isBefore(endTime) || tp.equals(endTime));
        }
    }

}
