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
     * Iteration count for the hashing function.
     */
    private static final int ITERATION_COUNT = 40000;

    /**
     * Key length of the generated secret key.
     */
    private static final int KEY_LENGTH = 128;

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
        String decryptedPassword = null;
        try {
            logger.info("Inside class RunConfigFile");

            File f = new File(configFilePath);
            if (!f.exists()) {
                logger.info("Inside class RunConfigFile,File does not exist");
                System.out.println("Config File does not exist, Please check provided path");
                return;
            }

            logger.info("Inside class RunConfigFile, File exist");
            BufferedReader b = new BufferedReader(new FileReader(f));
            ConfigurationReader configurationReader = new ConfigurationReader();
            logger.info("Inside class RunConfigFile, Geeting info from config file");
            Configuration config = configurationReader.read(b, logger);
            byte[] salt = new String("12345678").getBytes();

            // Decreasing this speeds down startup time and can be useful
            // during testing, but it also makes it easier for brute force
            // attackers

            if (config.getUsername() == null || config.getUsername().isEmpty() || config.getPassword() == null || config.getPassword().isEmpty()) {
                logger.info("Inside class RunConfigFile,Empty UserName or Password, Try Runnig Config file again");
                System.out.println("Empty UserName or Password, Try Runnig Config file again");
                return;
            }

            // password is decrypted from config file, and hence username and passowrd are checked if available
            logger.info("Inside class RunConfigFile, Username and Password is not empty");
            SecretKeySpec createSecretKey = SecurityHelper.createSecretKey(config.getUsername().toCharArray(),
                    salt, ITERATION_COUNT, KEY_LENGTH, logger);

            decryptedPassword = SecurityHelper.decrypt(config.getPassword(), createSecretKey, logger);

            if (config.getDevice() == null || config.getDevice().isEmpty() || config.getPump() == null || config.getPump().isEmpty()
                    || config.getSerialNumber() == null || config.getSerialNumber().isEmpty()) {
                logger.info("Inside class RunConfigFile,Empty Device or SN Number, Try changing or runnning Config file again");
                System.out.println("Empty Device or SN Number, Try changing or runnning Config file again");
                return;
            }


            Authentication auth = new Authentication();
            logger.info("Inside class RunConfigFile, device and SN number is not empty");

            if (auth.checkConnection(config.getUsername(), decryptedPassword, logger)) {

                String lang = auth.getLanguage();
                if (lang == null) {
                    System.out.println(
                            "Language of User logged in is not supporetd by Carelink Java program!! \n"
                                    + "Please try with user who has language as English or German");
                    return;
                }

                MouseSimulator simulator = new MouseSimulator();
                simulator.start(config.getUsername(), decryptedPassword, config.getDevice(), config.getPump(), config.getSerialNumber(), logger);
            }
        } catch (IOException e) {
            logger.info("Inside class RunConfigFile,error occured during checking config file");
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("Inside class RunConfigFile,Username or password was changed, Please initilize the config file once again");
            System.out.println("Username or password was changed, Please initilize the config file once again");
        }
    }
}
