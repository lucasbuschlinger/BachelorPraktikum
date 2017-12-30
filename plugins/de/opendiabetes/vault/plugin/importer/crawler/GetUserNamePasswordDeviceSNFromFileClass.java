package de.opendiabetes.vault.plugin.importer.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Extracts the relevant data from the reader buffer.
 */
class GetUserNamePasswordDeviceSNFromFileClass {

    /**
     * The reader offset for reading the password.
     */
    private static final int READER_PASSWORD_OFFSET = +9;


    /**
     * The reader offset for reading the csv path.
     */
    private static final int READER_PATH_TO_CSV_OFFSET = 17;

    /**
     * The current reading line of the reader.
     */
    private String readLine = "";

    /**
     * Username extracted from the reader.
     */
    private String userName = null;

    /**
     * Password extracted from the reader.
     */
    private String password = null;

    /**
     * Device extracted from the reader.
     */
    private String device = null;

    /**
     * Pump extracted from the reader.
     */
    private String pump = null;

    /**
     * SN extracted from the reader.
     */
    private String sn = null;

    /**
     * The path to the csv file extracted from the reader.
     */
    private String pathForCsv = null;

    /**
     * Extracts the username, password, device, pump and SN from the reader.
     *
     * @param b - a BufferedReader instance reading the csv file.
     * @param logger - a logger instance.
     * @return a string array containing the data.
     * @throws IOException - thrown if there was an error reading the file.
     */
     String[] getUsernamePassDevPumpSN(final BufferedReader b, final Logger logger) throws IOException {
        // TODO Auto-generated method stub
        logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass");
        while ((readLine = b.readLine()) != null) {

            if (readLine.toLowerCase().contains("username:")) {

                userName = readLine.substring(readLine.lastIndexOf(":") + 1);
                String tempUserNameString = userName.replaceAll("\t", "");
                tempUserNameString = tempUserNameString.replaceAll("\\s", "");

                if (tempUserNameString.indexOf(" ") > 0) {
                    tempUserNameString = tempUserNameString.substring(0, tempUserNameString.indexOf(" "));
                }

                userName = tempUserNameString;

                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get userName from command line input");
            }
            if (readLine.toLowerCase().contains("password:")) {
                password = readLine.substring(READER_PASSWORD_OFFSET);
                String tempPasswordString = password.replaceAll("\t", "");
                tempPasswordString = tempPasswordString.replaceAll("\\s", "");

                password = tempPasswordString;
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get password from command line input");

            }

            if (readLine.toLowerCase()
                    .contains("device:") /* && readLine.contains("#") */) {
                device = readLine.substring(readLine.lastIndexOf(":") + 1);
                String tempDeviceString = device.replaceAll("\t", "");
                tempDeviceString = tempDeviceString.replaceAll("\\s", "");

                if (tempDeviceString.indexOf("#") > 0) {
                    tempDeviceString = tempDeviceString.substring(0, tempDeviceString.indexOf("#"));
                }

                device = tempDeviceString.replaceAll("\\s+$", "");
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get device from command line input");
            }
            if (readLine.toLowerCase()
                    .contains("pump:") /* && readLine.contains("#") */) {
                pump = readLine.substring(readLine.lastIndexOf(":") + 1);
                String tempPumpString = pump.replaceAll("\t", "");
                tempPumpString = tempPumpString.replaceAll("\\s", "");

                if (tempPumpString.indexOf("#") > 0) {
                    tempPumpString = tempPumpString.substring(0, tempPumpString.indexOf("#"));
                }

                pump = tempPumpString.replaceAll("\\s+$", "");
                logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass, Get pump from command line input");
            }

            if (readLine.toLowerCase()
                    .contains("sn:") /* && readLine.contains("#") */) {
                sn = readLine.substring(readLine.lastIndexOf(":") + 1);
                String tempSNString = sn.replaceAll("\t", "");
                tempSNString = tempSNString.replaceAll("\\s", "");

                if (tempSNString.indexOf("#") > 0) {
                    tempSNString = tempSNString.substring(0, tempSNString.indexOf("#"));
                }

                sn = tempSNString.replaceAll("\\s+$", "");
                logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass, Get SN from command line input");
            }
            if (readLine.toLowerCase().contains("path to save csv:")) {
                pathForCsv = readLine.substring(READER_PATH_TO_CSV_OFFSET);
                String tempPathForCsvString = pathForCsv.replaceAll("\t", "");
                tempPathForCsvString = tempPathForCsvString.replaceAll("\\s", "");

                pathForCsv = tempPathForCsvString;
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get pathForCsv from command line input");
            }

        }
        return new String[]{userName, password, device, pump, sn, pathForCsv};
    }
}
