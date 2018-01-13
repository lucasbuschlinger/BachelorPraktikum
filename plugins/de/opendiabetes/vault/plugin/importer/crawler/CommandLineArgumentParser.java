package de.opendiabetes.vault.plugin.importer.crawler;

import org.apache.commons.cli.*;

import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Class to parse argument flags from command line.
 */
public class CommandLineArgumentParser {

    /**
     * File path pointing to the config file.
     */
    private String configFilePath = null;

    /**
     * The path where the csv file should be downloaded to.
     */
    private String csvDownloadOutputPath = null;

    /**
     * The date from when the csv file data should start.
     */
    private String fromDate = null;

    /**
     * The date until when the csv file data should go.
     */
    private String toDate = null;

    /**
     * This counter will check if it is
     * 0 then at very last throw a error
     * or else different modules have
     * already handled error with
     * appropriate message.
     */
    private int tempCounterForThrowingError = 0;

    /**
     * The number of iterations for the hashing algorithm.
     *
     * Decreasing this speeds down startup time and
     * can be useful during testing,
     * but it also makes it easier for brute force attackers.
     */
    private static final int ITERATION_COUNT = 40000;

    /**
     * The length of the key.
     * Other values give me java.security.InvalidKeyException: Illegal key
     * size or default parameters.
     */
    private static final int KEY_LENGTH = 128;

    /**
     * Will run program depending on flag/argument chosen.
     *
     * @param args - the arguments for the application.
     * @param logger - a logger instance.
     * @throws IOException - thrown if any input/output failure occurred.
     * @throws ParseException - thrown if there was an error parsing the arguments.
     * @throws SecurityException - thrown if there was a security error.
     * @throws AWTException - thrown if there was a awt error.
     * @throws InterruptedException - thrown if there was a threading error.
     * @throws java.text.ParseException - thrown if there was an error parsing the arguments.
     * @throws GeneralSecurityException - thrown if there was a security error.
     */
    public void runDifferentArguments(final String[] args, final Logger logger)
            throws IOException, ParseException, SecurityException,
            AWTException, InterruptedException, java.text.ParseException, GeneralSecurityException {

        logger.info("Inside Class FlagArgumentsClass");
        CommandLine commandLine; // to get command line arguments
        if (!(args.length > 0)) {
            logger.info("Arguments missing");
            System.out.println("Arguments missing");
            return;
        }

        // If there are any argument provided, program will go furthure and
        // check different modules
			/*
			 * If first argument is for help, all other arguments will be
			 * ignored
			 */

        Options optionsForHelp = new Options();

        Option shortHelpOptions = Option.builder("h").required(false).desc("The short help option").longOpt("help").build();
        optionsForHelp.addOption((org.apache.commons.cli.Option) shortHelpOptions);

        commandLine = new DefaultParser().parse(optionsForHelp, args, true);
        if (commandLine.getOptions().length != 0) {
            if (commandLine.getOptions()[0].getOpt() == "h") {
                System.out.println("Version: Carelink Crawler v0.1\n" + "Options: "
                        + "	  -v,--version       	show program's version number and exit\n"
                        + "   -h, --help         	show this help message and exit\n"
                        + "   -i, --init FILE	 	initializes a new config file at the given path.\n"
                        + "   -c, --config FILE	 	defines the used config file.\n"
                        + "   -o,--output-path FILE defines output path (Default is ./).\n"
                        + "   -crawler				starts in crawler mode. -from and -to is required.\n"
                        + "   -from					defines start time point for the dataset.\n"
                        + "   -to					defines end time point for the dataset.\n"
                        + "   -u, --upload 			starts in upload mode.\n"
                        + "   Combination to initilize a program : java -jar XYZ.jar -i,--init\n"
                        + "   Combination to run upload program : java -jar XYZ.jar -c or --config completepathwithfilename "
                        + " -u,--upload\n"
                        + "   Combination to run crawler program : java -jar XYZ.jar -c,--config completepathwithfilename "
                        + "-crawler -from 15/05/2017 -to 20/06/2017 --output outputfolderpath \n"
                        + "Date Format:\n"
                        + "English: DD/MM/YYYY\n"
                        + "German: DD.MM.YYYY\n");
                tempCounterForThrowingError++;
                return;
            }
        }

			/*
			 * If first argument is for version, all other arguments will be
			 * ignored
			 */

        Options optionForVersion = new Options();
        Option shortVersionOptions = Option.builder("v").required(false).desc("The short version option").longOpt("version")
                .build();
        optionForVersion.addOption((org.apache.commons.cli.Option) shortVersionOptions);
        commandLine = new DefaultParser().parse(optionForVersion, args, true);
        if (commandLine.getOptions().length != 0) {
            if (commandLine.getOptions()[0].getOpt() == "v") {
                System.out.println("Version: Carelink Crawler v0.1\n");
                tempCounterForThrowingError++;
                return;
            }
        }


            /*
             * If first argument is for initilization, all other arguments
             * will be ignored
             */
        Options optionForInitilization = new Options();

        Option shortInitOptions = Option.builder("i").required(false).desc("The init option").longOpt("init").build();

        optionForInitilization.addOption((org.apache.commons.cli.Option) shortInitOptions);

        commandLine = new DefaultParser().parse(optionForInitilization, args, true);
        if (commandLine.getOptions().length != 0) {
            if (commandLine.getOptions()[0].getOpt() == "i") {
                logger.info("User input flag such as -u or -c or -init");
                logger.info("Input entered by user is for initilizing config file");
                Scanner reader = new Scanner(System.in); // Reading from
                // System.in
                System.out.println("Enter Username: ");
                String username = reader.nextLine();

                System.out.println("Inside u");
                Console console = System.console();
                String password = null;
                if (console == null) {
						/*
						 * Here if Program runs from any IDE PasswordField will
						 * be popped up, because we cannot mask password using
						 * IDE
						 */
                    //System.err.println("No console.");
                    System.out.println("Enter password: ");
                    password = reader.nextLine();
                    logger.info("Ask user for password");
//						final JPasswordField pf = new JPasswordField();
//						password = JOptionPane.showConfirmDialog(null, pf, "Password", JOptionPane.OK_CANCEL_OPTION,
//								JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION ? new String(pf.getPassword())
//										: "";
                } else {
                    logger.info("Ask user for password");
                    password = new String(console.readPassword("Password: "));
                }
                // Class to create config file with user given details
                CreateConfigFile config = new CreateConfigFile();
                config.createFile(username, password, logger);
                tempCounterForThrowingError++;

                return;

            }
        }


			/*
			 * *****************************************
			 * If arguments entered
			 * are not for help, version, initilization then rest of the program
			 * checks for other remaining options
			 * ****************************************
			 */

        Option shortUploadOptions = Option.builder("u").required(false).desc("The U option").longOpt("upload").build();

        Option crawlerOptions = Option.builder("crawler").required(false).desc("The Crawler option").build();

        Option fromDateOptions = Option.builder("from").hasArg().required(false).desc("The from option").build();

        Option toDateOptions = Option.builder("to").hasArg().required(false).desc("The to option").build();

        Option shortConfigOptions = Option.builder("c").hasArg().required(false).desc("The c option").longOpt("config").build();

        Option shortOutputOptions = Option.builder("o").hasArg().required(false).desc("The o option").longOpt("output").build();


        Options options = new Options();
        options.addOption((org.apache.commons.cli.Option) shortUploadOptions);


        options.addOption((org.apache.commons.cli.Option) crawlerOptions);

        options.addOption((org.apache.commons.cli.Option) fromDateOptions);

        options.addOption((org.apache.commons.cli.Option) toDateOptions);

        options.addOption((org.apache.commons.cli.Option) shortConfigOptions);


        options.addOption((org.apache.commons.cli.Option) shortOutputOptions);


        // Command line argument into list
        ArrayList<String> argFromCommandLine = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            argFromCommandLine.add(args[i]);
        }

        String[] commandLineLocalArg = new String[argFromCommandLine.size()];
        commandLineLocalArg = argFromCommandLine.toArray(commandLineLocalArg); // getting all command line

        try {
            logger.info("Getting all the user input flag and its value");
            commandLine = new DefaultParser().parse(options, args, true);

            if (commandLine.hasOption("o")) {
                csvDownloadOutputPath = commandLine.getOptionValue("o");
            }

            if (commandLine.hasOption("c")) {
                configFilePath = commandLine.getOptionValue("c");
            }

            if (commandLine.hasOption("from")) {
                fromDate = commandLine.getOptionValue("from");
            }
            if (commandLine.hasOption("to")) {
                toDate = commandLine.getOptionValue("to");
            }

            /*** REMOVED TEST COMMANDS FOR NOW ***/
            /****************************************
             * Testing Upload section
             */
/*



                if (commandLine.getOptions().length > 1) {
                    if (((commandLine.getOptions()[0].getOpt() == "test")
                            && (commandLine.getOptions()[1].getOpt() == "u"))
                            || ((commandLine.getOptions()[0].getOpt() == "u")
                            && (commandLine.getOptions()[1].getOpt() == "test"))) {
                        System.out.println("Starting Unit Test case for Applet Wrapper");

                        UnitTestCases.TestAppletWrapper TestApplet = new UnitTestCases.TestAppletWrapper();
                        System.out.println("Unit Test case for From Device and SN Number");
                        TestApplet.deviceAndSNTest();

                        UnitTestCases.TestLoginDetails TestLogin = new UnitTestCases.TestLoginDetails();
                        System.out.println("Unit Test case for UserName and Password");
                        TestLogin.isLoginCorrect();
                        TempCounterforThrowingerror++;
                        return;

                    }
                }



                /****************************************
                 * Testing Download i.e crawler section


                if (commandLine.getOptions().length > 1) {
                    if (((commandLine.getOptions()[0].getOpt() == "test")
                            && (commandLine.getOptions()[1].getOpt() == "crawler"))
                            || ((commandLine.getOptions()[0].getOpt() == "crawler")
                            && (commandLine.getOptions()[1].getOpt() == "test"))) {
                        System.out.println("Starting Unit Test case for Crawler");

                        UnitTestCases.TestDatesClass TestDates = new UnitTestCases.TestDatesClass();
                        System.out.println("Unit Test case for From Date and To Date");
                        TestDates.AreDatesCorrect();

                        UnitTestCases.TestLoginDetails TestLogin = new UnitTestCases.TestLoginDetails();

                        System.out.println("Unit Test case for UserName and Password");
                        TestLogin.isLoginCorrect();

                        UnitTestCases.TestPathToSaveData TestPath = new UnitTestCases.TestPathToSaveData();
                        System.out.println("Unit Test case for Path to Save CSV");
                        TestPath.checkPath();
                        TempCounterforThrowingerror++;
                        return;

                    }
                }
*/
            Boolean isInsideUpload = false;
            if (commandLine.getOptions().length > 1) {
                if ((commandLine.getOptions()[0].getOpt() == "c" && commandLine.getOptions()[1].getOpt() == "u")
                        || (commandLine.getOptions()[1].getOpt() == "c" || commandLine.getOptions()[0].getOpt() == "u")) {
                    isInsideUpload = true;
                }
            }

            if (isInsideUpload) {
                logger.info("Inside upload");
                System.out.println("Starting upload Program");
                RunConfigFile runConfig = new RunConfigFile();
                runConfig.runFile(logger, configFilePath);
                tempCounterForThrowingError++;
                return;
            }


            if (!commandLine.hasOption("c")) {
                tempCounterForThrowingError++;
                return;
            }

            if (!commandLine.hasOption("from") || !commandLine.hasOption("to") || !commandLine.hasOption("crawler")) {
                tempCounterForThrowingError++;
                return;
            }

            // To generate CSV document
            logger.info("Input entered by user is for Crawling");
            System.out.println("Starting Crawler Program");

//						CheckDatesClass checkdates = new CheckDatesClass();
//						if (checkdates.getStratDate(fromDate, logger)) {
//
//							logger.info("from date is correct");
//
//							if (checkdates.getEndDate(fromDate, toDate, logger)) {
//
//								logger.info("End date is correct");
            try {
                logger.info("Inside logic for checking config file availabe for crawler");
                Path currentRelativePath = Paths.get("");

                File f = new File(configFilePath);
                if (!f.exists()) {
                    logger.info("config file does not exist");
                    System.out.println("File does not exist");
                    return;
                }

                logger.info("config file is availabe");
                BufferedReader b = new BufferedReader(new FileReader(f));
                ConfigurationReader configurationReader = new ConfigurationReader();

                Configuration configuration = configurationReader.read(b, logger);

                /*************
                 *
                 * metadata[0] =Username
                 * metadata[1] = Password
                 * metadata[2] = device
                 * metadata[3] = pump
                 *  metadata[4] = SN
                 */

                logger.info("Function is called to get Encrypted username and passowrd");

                byte[] salt = new String("12345678").getBytes();

                if (configuration.getUsername() == null
                        || configuration.getUsername().isEmpty()
                        || configuration.getPassword() == null
                        || configuration.getPassword().isEmpty()
                                /*|| metadata[4] == null & metadata[4].isEmpty()*/) {
                    logger.info("username and passowrd is empty");
                    return;
                }

                logger.info("username and passowrd is not empty");
                SecretKeySpec createSecretKey = SecurityHelper
                        .createSecretKey(configuration.getUsername().toCharArray(), salt,
                                ITERATION_COUNT, KEY_LENGTH, logger);
                String decryptedPassword = SecurityHelper.decrypt(configuration.getPassword(),
                        createSecretKey, logger);
                Authentication auth = new Authentication();
                if (!auth.checkConnection(configuration.getUsername(), decryptedPassword, logger)) {
                    logger.info("username and passowrd entered are incorrect");
                    return;
                }

                logger.info("username and passowrd Enetered are correct");
                String lang = auth.getLanguage();
                if (lang == null) {
                    System.out.println(
                            "Language of User logged in is not supporetd by Carelink Java program!! \n"
                                    + "Please try with user who has language as English or German");
                    return;
                }

                DateHelper dateHelper = new DateHelper(lang);
                if (!dateHelper.getStartDate(fromDate, logger)) {
                    logger.info("from date is incorrect");
                    return;
                }

                logger.info("from date is correct");

                if (!dateHelper.getEndDate(fromDate, toDate, logger)) {
                    logger.info("End date is incorrect");
                    return;
                }

                logger.info("End date is correct");

                Crawler crawler = new Crawler();

                if (commandLine.hasOption("o")) {
                    crawler.generateDocument(auth.getcookies(), fromDate,
                            toDate, csvDownloadOutputPath, logger, configFilePath);
                } else {
                    String userHomepath = System.getProperty("user.dir");
                    crawler.generateDocument(auth.getcookies(), fromDate, toDate, userHomepath, logger, configFilePath);
                }

                logger.info("CSV FIle generated");
            } catch (IOException e) {
                logger.info("Issue with getting file");
                e.printStackTrace();
            } catch (Exception e) {
                logger.info("Username or password was changed in config file");
                System.out.println("Username or password or Path to save CSV was changed,"
                        + " Please initilize the config file once again");
            }

        } catch (ParseException exception) {
            tempCounterForThrowingError++;
            System.out.print("Parse error: ");
            System.out.println(exception.getMessage());
        }

        if (tempCounterForThrowingError == 0) {
            System.out.println(
                    "Arguments are not in correct combination please try --help or -h for getting correct combination");
        }
    }
}
