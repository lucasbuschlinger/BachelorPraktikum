package de.opendiabetes.vault.plugin.importer.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;


public class GetUserNamePasswordDeviceSNFromFileClass {
    String readLine = "";
    String userName = null, password = null, device = null, pump = null, SN = null, decrypetedPassowrd = null,
            pathForCsv = null;

    public String[] getUsernamePassDevPumpSN(BufferedReader b, Logger logger) throws IOException {
        // TODO Auto-generated method stub
        logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass");
        while ((readLine = b.readLine()) != null) {

            if (readLine.toLowerCase().contains("username:")) {

                userName = readLine.substring(readLine.lastIndexOf(":") + 1);
                String TempUserNameString = userName.replaceAll("\t", "");
                TempUserNameString = TempUserNameString.replaceAll("\\s", "");

                TempUserNameString = TempUserNameString.indexOf(" ") > 0
                        ? TempUserNameString.substring(0, TempUserNameString.indexOf(" ")) : TempUserNameString;

                userName = TempUserNameString;

                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get userName from command line input");
            }
            if (readLine.toLowerCase().contains("password:")) {
                password = readLine.substring(+9);
                String TempPasswordString = password.replaceAll("\t", "");
                TempPasswordString = TempPasswordString.replaceAll("\\s", "");

                password = TempPasswordString;
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get password from command line input");

            }

            if (readLine.toLowerCase()
                    .contains("device:") /* && readLine.contains("#") */) {
                device = readLine.substring(readLine.lastIndexOf(":") + 1);
                String TempDeviceString = device.replaceAll("\t", "");
                TempDeviceString = TempDeviceString.replaceAll("\\s", "");

                TempDeviceString = TempDeviceString.indexOf("#") > 0
                        ? TempDeviceString.substring(0, TempDeviceString.indexOf("#")) : TempDeviceString;

                device = TempDeviceString.replaceAll("\\s+$", "");
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get device from command line input");
            }
            if (readLine.toLowerCase()
                    .contains("pump:") /* && readLine.contains("#") */) {
                pump = readLine.substring(readLine.lastIndexOf(":") + 1);
                String TemppumpString = pump.replaceAll("\t", "");
                TemppumpString = TemppumpString.replaceAll("\\s", "");

                TemppumpString = TemppumpString.indexOf("#") > 0
                        ? TemppumpString.substring(0, TemppumpString.indexOf("#")) : TemppumpString;

                pump = TemppumpString.replaceAll("\\s+$", "");
                logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass, Get pump from command line input");
            }

            if (readLine.toLowerCase()
                    .contains("sn:") /* && readLine.contains("#") */) {
                SN = readLine.substring(readLine.lastIndexOf(":") + 1);
                String TempSNString = SN.replaceAll("\t", "");
                TempSNString = TempSNString.replaceAll("\\s", "");

                TempSNString = TempSNString.indexOf("#") > 0 ? TempSNString.substring(0, TempSNString.indexOf("#"))
                        : TempSNString;

                SN = TempSNString.replaceAll("\\s+$", "");
                logger.info("Inside class GetUserNamePasswordDeviceSNFromFileClass, Get SN from command line input");
            }
            if (readLine.toLowerCase().contains("path to save csv:")) {
                pathForCsv = readLine.substring(17);
                String TempPathForCsvString = pathForCsv.replaceAll("\t", "");
                TempPathForCsvString = TempPathForCsvString.replaceAll("\\s", "");

                pathForCsv = TempPathForCsvString;
                logger.info(
                        "Inside class GetUserNamePasswordDeviceSNFromFileClass, Get pathForCsv from command line input");
            }

        }
        return new String[]{userName, password, device, pump, SN, pathForCsv};
    }
}
