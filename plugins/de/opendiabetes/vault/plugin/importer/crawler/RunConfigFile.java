package de.opendiabetes.vault.plugin.importer.crawler;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

/**
 * Class for reading the config file.
 */
public class RunConfigFile {

    /**
     * Array index of the username.
     */
    private static final int USERNAME_INDEX = 0;

    /**
     * Array index of the password.
     */
    private static final int PASSWORD_INDEX = 1;

    /**
     * Array index of the device.
     */
    private static final int DEVICE_INDEX = 2;

    /**
     * Array index of the pump.
     */
    private static final int PUMP_INDEX = 3;

    /**
     * Array index of the sn.
     */
    private static final int SN_INDEX = 4;

    /**
     * Iteration count for the hashing function.
     */
    private static final int ITERATION_COUNT = 40000;

    /**
     * Key length of the generated secret key.
     */
    private static final int KEY_LENGTH = 128;

    /**
     * Holds the username, password, device, pump and sn read from the config file.
     */
    private String[] uPDSArray;

    /**
     * Decrypted password of the user.
     */
    private String decryptedPassowrd = null;

    /**
     * Reads the given config file.
     *
     * @param logger - a logger instance
     * @param configFilePath - path to the config file
     * @throws SecurityException - thrown if there was an error decrypting the password
     * @throws AWTException - thrown if there was an error crawling
     * @throws InterruptedException - thrown if there was an multithreading exception
     * @throws GeneralSecurityException - thrown if there was an error decrypting the password
     */
    void runFile(final Logger logger, final String configFilePath)
            throws SecurityException, AWTException, InterruptedException, GeneralSecurityException {
        // TODO Auto-generated method stub
        try {
            logger.info("Inside class RunConfigFile");

            File f = new File(configFilePath);
            if (f.exists()) {
                logger.info("Inside class RunConfigFile, File exist");
                BufferedReader b = new BufferedReader(new FileReader(f));
                MetadataExtractor extractor = new MetadataExtractor();
                logger.info("Inside class RunConfigFile, Geeting info from config file");
                uPDSArray = extractor.getUsernamePassDevPumpSN(b, logger);
                byte[] salt = new String("12345678").getBytes();

                // Decreasing this speeds down startup time and can be useful
                // during testing, but it also makes it easier for brute force
                // attackers

                /*************
                 *
                 * uPDSArray[0] =Username
                 * uPDSArray[1] = Password
                 * uPDSArray[2] = device
                 * uPDSArray[3] = pump
                 *  uPDSArray[4] = SN
                 */

                final String username = uPDSArray[USERNAME_INDEX];
                final String password = uPDSArray[PASSWORD_INDEX];
                final String device = uPDSArray[DEVICE_INDEX];
                final String pump = uPDSArray[PUMP_INDEX];
                final String sn = uPDSArray[SN_INDEX];
                try {
                    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {

                        // password is decrypted from config file, and hence username and passowrd are checked if available

                        logger.info("Inside class RunConfigFile, Username and Password is not empty");
                        SecretKeySpec createSecretKey = SecurityHelper.createSecretKey(username.toCharArray(),
                                salt, ITERATION_COUNT, KEY_LENGTH, logger);

                        decryptedPassowrd = SecurityHelper.decrypt(password, createSecretKey, logger);

                        if (device != null && !device.isEmpty() && pump != null && !pump.isEmpty()
                                && sn != null && !sn.isEmpty()) {

                            LoginDetailsClass loginDetails = new LoginDetailsClass();
                            logger.info("Inside class RunConfigFile, device and SN number is not empty");

                            if (loginDetails.checkConnection(username, decryptedPassowrd, logger)) {

                                String lang = loginDetails.getLanguage();
                                if (lang == null) {
                                    System.out.println(
                                            "Language of User logged in is not supporetd by Carelink Java program!! \n"
                                                    + "Please try with user who has language as English or German");
                                    return;
                                }

                                SimulateMouseClass sm = new SimulateMouseClass();

                                sm.startMouseClicks(username, decryptedPassowrd, device, pump, sn, logger);

                            }
                        } else {
                            logger.info("Inside class RunConfigFile,Empty Device or SN Number, Try changing or runnning Config file again");
                            System.out.println("Empty Device or SN Number, Try changing or runnning Config file again");
                        }
                    } else {
                        logger.info("Inside class RunConfigFile,Empty UserName or Password, Try Runnig Config file again");
                        System.out.println("Empty UserName or Password, Try Runnig Config file again");
                    }
                } catch (Exception e) {
                    logger.info("Inside class RunConfigFile,Username or password was changed, Please initilize the config file once again");
                    System.out.println("Username or password was changed, Please initilize the config file once again");
                }
            } else {
                logger.info("Inside class RunConfigFile,File does not exist");
                System.out.println("Config File does not exist, Please check provided path");
            }
        } catch (IOException e) {
            logger.info("Inside class RunConfigFile,error occured during checking config file");
            e.printStackTrace();
        }
    }
}
