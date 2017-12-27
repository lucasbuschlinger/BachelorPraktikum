package de.opendiabetes.vault.plugin.importer.crawler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Implementation to check dates.
 */
public class CheckDatesClass {

    /**
     * The date format for the current localisation.
     */
    private static String dateFormat;

    /**
     * The valid start date.
     */
    private Date validStartDate;

    /**
     * The formatted version of the entered start date.
     */
    private Date formattedEnteredStartDate;

    /**
     * The formatted version of the entered end date.
     */
    private Date formattedEnteredEndDate;

    /**
     * A date formation error message.
     */
    private String dateFormatErrorMessage;

    /**
     * A todays date error message.
     */
    private String todaysDateErrorMessage;

    /**
     * Constructor.
     *
     * @param lang - language of the date format.
     * @throws IllegalArgumentException - thrown if the given argument is null.
     * @throws ParseException - thrown if the date format cannot be parsed.
     */
    public CheckDatesClass(final String lang) throws IllegalArgumentException, ParseException {

        if (lang == null) {
            throw new IllegalArgumentException("Language is not allowed to be null");
        }

        if (lang.equals("de")) {
            dateFormat = "dd.MM.yyyy";
            validStartDate = new SimpleDateFormat(dateFormat).parse("01.01.1998");
            dateFormatErrorMessage = "Date should be in Format of DD/MM/YYYY  Example: 13.03.2017";
            todaysDateErrorMessage = "You can only enter Start date between 01/01/1998 and Today's Date!!";
        } else if (lang.equals("en")) {
            dateFormat = "dd/MM/yyyy";
            validStartDate = new SimpleDateFormat(dateFormat).parse("01/01/1998");
            dateFormatErrorMessage = "Date should be in Format of DD/MM/YYYY  Example: 13/03/2017";
            todaysDateErrorMessage = "You can only enter Start date between 01/01/1998 and Today's Date!!";
        } else {
            throw new IllegalArgumentException("Language is not allowed to be any other string than \"de\" or \"en\"");
        }
    }

    /**
     * Checks and validates the given fromDate.
     *
     * 1. Date format should be DD/MM/YYYY.
     * 2. Start date and end date should not be before 01/01/1998.
     * 3. End date should not be greater than start date.
     * 4. Start date and end date shall not be greater than Today's date.
     * 5. Start date and end date should be valid.
     *
     * This class and it's function satisfies above conditions for dates
     *
     * @param fromDate - the fromDate to be validated
     * @param logger - a logger instance
     * @return a boolean whether the date has a correct or incorrect format
     * @throws ParseException - thrown if the given date could not be parsed
     */
    public Boolean getStratDate(final String fromDate, final Logger logger) throws ParseException {

        logger.info("Inside Class CheckDatesClass, Method getStratDate");
        SimpleDateFormat formatTodayDate = new SimpleDateFormat(dateFormat); // to format today's date as DD/MM/YYYY
        formatTodayDate.setLenient(false);
        String todayDate = formatTodayDate.format(new Date());

        // Date StiatcValidStartDate = new SimpleDateFormat(dateFormat).parse("01/01/1998");
        /*
         * ********
         *  In carelink website this date is the starting date to download report
         * **********
         */
        try {
            formattedEnteredStartDate = new SimpleDateFormat(dateFormat).parse(fromDate);
        } catch (Exception e) {
            logger.info("Start Date is not in correct format \n" + dateFormatErrorMessage);
            System.out.println(
                    "Start Date is not in correct format \n" + dateFormatErrorMessage);
            return false;
        }
        try {
            DateFormat validStartdate = new SimpleDateFormat(dateFormat); // To validate start date
            validStartdate.setLenient(false);
            validStartdate.parse(fromDate);
            if (formattedEnteredStartDate.before(validStartDate)
                    || formattedEnteredStartDate.after(new SimpleDateFormat(dateFormat).parse(todayDate))) {
                System.out.println(todaysDateErrorMessage);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.info("Start Date is not Valid");
            System.out.println("Start Date is not Valid");
            return false;

        }

    }

    /**
     * Checks whether the given end date is valid.
     *
     * @param startDate - a start date.
     * @param endDate - an end date.
     * @param logger - a logger instance.
     * @return a boolean whether the given dates are valid
     * @throws ParseException - thrown if the given date could not be parsed
     */
    public boolean getEndDate(final String startDate, final String endDate, final Logger logger) throws ParseException {
        logger.info("Inside Class CheckDatesClass, Method getEndDate");
        SimpleDateFormat formatTodayDate = new SimpleDateFormat(dateFormat);
        String todayDate = formatTodayDate.format(new Date());

        // Date StiatcValidStartDate = new SimpleDateFormat(dateFormat).parse("01/01/1998");
        formattedEnteredStartDate = new SimpleDateFormat(dateFormat).parse(startDate);

        try {
            formattedEnteredEndDate = new SimpleDateFormat(dateFormat).parse(endDate);
        } catch (Exception e) {
            logger.info("End Date is not in correct format \n" + dateFormatErrorMessage);
            System.out.println(
                    "End Date is not in correct format \n" + dateFormatErrorMessage);
            return false;
        }
        try {
            DateFormat validEndDate = new SimpleDateFormat(dateFormat); // To validate End date
            validEndDate.setLenient(false);
            validEndDate.parse(endDate);
            if (formattedEnteredEndDate.after(new SimpleDateFormat(dateFormat).parse(todayDate))
                    || formattedEnteredEndDate.before(validStartDate)) {
                System.out.println(todaysDateErrorMessage);
                return false;
            }
            if (formattedEnteredEndDate.before(formattedEnteredStartDate)) {
                System.out.println("End Date cannot be earlier than Start date");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.info("End Date is not Valid");
            System.out.println("End Date is not Valid");
            return false;
        }
    }


}
