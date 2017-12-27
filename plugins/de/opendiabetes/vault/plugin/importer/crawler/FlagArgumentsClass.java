package de.opendiabetes.vault.plugin.importer.crawler;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FlagArgumentsClass {

    String UPDSArray[];
    String decrypetedPassowrd = null;
    String username = null;
    String password = null;
    String configFilePath = null;
    String CsvDownloadOutputPath = null;
    String fromDate = null, toDate = null;
    int TempCounterforThrowingerror = 0; /*
                                             * This counter will check if it is
											 * 0 then at very last throw a error
											 * or else different modules have
											 * already handled error with
											 * appropriate message
											 */

    // Below function will run program depending on flag/argument chosen
    void RunDifferentArguments(String[] args, Logger logger) throws IOException, ParseException, SecurityException,
            AWTException, InterruptedException, java.text.ParseException, GeneralSecurityException {

        logger.info("Inside Class FlagArgumentsClass");
        CommandLine commandLine; // to get command line arguments
        if (!(args.length > 0)) {
            logger.info("Arguments missing");
            System.out.println("Arguments missing");
        } else {
            // If there are any argument provided, program will go furthure and
            // check different modules
			/*
			 * If first argument is for help, all other arguments will be
			 * ignored
			 */

            Options OptionsForHelp = new Options();

            Option option_help_short = Option.builder("h").required(false).desc("The short help option").longOpt("help").build();
            OptionsForHelp.addOption((org.apache.commons.cli.Option) option_help_short);

            commandLine = new DefaultParser().parse(OptionsForHelp, args, true);
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
                    TempCounterforThrowingerror++;
                    return;
                }
            }

			/*
			 * If first argument is for version, all other arguments will be
			 * ignored
			 */

            Options OptionForVersion = new Options();
            Option option_version_short = Option.builder("v").required(false).desc("The short version option").longOpt("version")
                    .build();
            OptionForVersion.addOption((org.apache.commons.cli.Option) option_version_short);
            commandLine = new DefaultParser().parse(OptionForVersion, args, true);
            if (commandLine.getOptions().length != 0) {
                if (commandLine.getOptions()[0].getOpt() == "v") {
                    System.out.println("Version: Carelink Crawler v0.1\n");
                    TempCounterforThrowingerror++;
                    return;
                }
            }


				/*
				 * If first argument is for initilization, all other arguments
				 * will be ignored
				 */
            Options OptionForInitilization = new Options();

            Option option_Init_short = Option.builder("i").required(false).desc("The init option").longOpt("init").build();

            OptionForInitilization.addOption((org.apache.commons.cli.Option) option_Init_short);

            commandLine = new DefaultParser().parse(OptionForInitilization, args, true);
            if (commandLine.getOptions().length != 0) {
                if (commandLine.getOptions()[0].getOpt() == "i") {
                    logger.info("User input flag such as -u or -c or -init");
                    logger.info("Input entered by user is for initilizing config file");
                    Scanner reader = new Scanner(System.in); // Reading from
                    // System.in
                    System.out.println("Enter Username: ");
                    username = reader.nextLine();

                    System.out.println("Inside u");
                    Console console = System.console();
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
                    TempCounterforThrowingerror++;

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

            Option option_Upload_short = Option.builder("u").required(false).desc("The U option").longOpt("upload").build();

            Option option_Crawler = Option.builder("crawler").required(false).desc("The Crawler option").build();

            Option option_FromDate = Option.builder("from").hasArg().required(false).desc("The from option").build();

            Option option_ToDate = Option.builder("to").hasArg().required(false).desc("The to option").build();

            Option option_config_short = Option.builder("c").hasArg().required(false).desc("The c option").longOpt("config").build();

            Option option_output_short = Option.builder("o").hasArg().required(false).desc("The o option").longOpt("output").build();


            Options options = new Options();
            options.addOption((org.apache.commons.cli.Option) option_Upload_short);


            options.addOption((org.apache.commons.cli.Option) option_Crawler);

            options.addOption((org.apache.commons.cli.Option) option_FromDate);

            options.addOption((org.apache.commons.cli.Option) option_ToDate);

            options.addOption((org.apache.commons.cli.Option) option_config_short);


            options.addOption((org.apache.commons.cli.Option) option_output_short);


            // Command line argument into list
            ArrayList<String> argFromCommandLine = new ArrayList<String>();
            for (int i = 0; i < args.length; i++) {

                argFromCommandLine.add(args[i]);
            }

            String[] commandLineLocalArg = new String[argFromCommandLine.size()];
            commandLineLocalArg = argFromCommandLine.toArray(commandLineLocalArg); // getting
            // all
            // command
            // line

            try {
                logger.info("Getting all the user input flag and its value");

                commandLine = new DefaultParser().parse(options, args, true);

                if (commandLine.hasOption("o")) {

                    CsvDownloadOutputPath = commandLine.getOptionValue("o");

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

                // If no argument is passed it will show below statement
                {

                    if (!(args.length > 0)) {
                        logger.info("Arguments missing");
                        System.out.println("Arguments missing");
                        return;
                    }

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

                Boolean Isupload = false;
                if (commandLine.getOptions().length > 1) {
                    if ((commandLine.getOptions()[0].getOpt() == "c" && commandLine.getOptions()[1].getOpt() == "u") ||
                            (commandLine.getOptions()[1].getOpt() == "c" || commandLine.getOptions()[0].getOpt() == "u")) {

                        Isupload = true;
                    }
                }

                if (Isupload) {

                    logger.info("Inside upload");
                    System.out.println("Starting upload Program");
                    RunConfigFile RunConfig = new RunConfigFile();
                    RunConfig.runFile(logger, configFilePath);
                    TempCounterforThrowingerror++;
                    return;
                }


                if (commandLine.hasOption("c")) {

                    if (commandLine.hasOption("from") && commandLine.hasOption("to")
                            && commandLine.hasOption("crawler")) {
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
                            if (f.exists()) {
                                logger.info("config file is availabe");
                                BufferedReader b = new BufferedReader(new FileReader(f));
                                GetUserNamePasswordDeviceSNFromFileClass GetUPDS = new GetUserNamePasswordDeviceSNFromFileClass();

                                UPDSArray = GetUPDS.getUsernamePassDevPumpSN(b, logger);

                                /*************
                                 *
                                 * UPDSArray[0] =Username
                                 * UPDSArray[1] = Password
                                 * UPDSArray[2] = device
                                 * UPDSArray[3] = pump
                                 *  UPDSArray[4] = SN
                                 */

                                logger.info("Function is called to get Encrypted username and passowrd");

                                byte[] salt = new String("12345678").getBytes();

                                // Decreasing this speeds down startup
                                // time
                                // and can be useful
                                // during testing, but it also makes it
                                // easier for brute force
                                // attackers
                                int iterationCount = 40000;
                                int keyLength = 128;
                                try {
                                    if (UPDSArray[0] != null & !UPDSArray[0].isEmpty() && UPDSArray[1] != null
                                            && !UPDSArray[1].isEmpty()
													/*&& UPDSArray[4] != null & !UPDSArray[4].isEmpty()*/) {
                                        logger.info("username and passowrd is not empty");
                                        SecretKeySpec createSecretKey = CreateSecurePasswordClass
                                                .createSecretKey(UPDSArray[0].toCharArray(), salt,
                                                        iterationCount, keyLength, logger);
                                        decrypetedPassowrd = CreateSecurePasswordClass.decrypt(UPDSArray[1],
                                                createSecretKey, logger);
                                        LoginDetailsClass LoginDetails = new LoginDetailsClass();
                                        if (LoginDetails.checkConnection(UPDSArray[0], decrypetedPassowrd,
                                                logger)) {
                                            logger.info("username and passowrd Enetered are correct");
                                            String Lang = LoginDetails.GetLanguage();
                                            if (Lang == null) {
                                                System.out.println(
                                                        "Language of User logged in is not supporetd by Carelink Java program!! \n"
                                                                + "Please try with user who has language as English or German");
                                                return;
                                            }

                                            CheckDatesClass checkdates = new CheckDatesClass(Lang);
                                            if (checkdates.getStratDate(fromDate, logger)) {

                                                logger.info("from date is correct");

                                                if (checkdates.getEndDate(fromDate, toDate, logger)) {

                                                    logger.info("End date is correct");


                                                    if (commandLine.hasOption("o")) {
                                                        CrawlerClass Crawler = new CrawlerClass();
                                                        Crawler.generateDocument(LoginDetails.getcookies(), fromDate,
                                                                toDate, CsvDownloadOutputPath, logger, configFilePath);
                                                    } else {
                                                        String userHomepath = System.getProperty("user.dir");
                                                        CrawlerClass Crawler = new CrawlerClass();
                                                        Crawler.generateDocument(LoginDetails.getcookies(), fromDate,
                                                                toDate, userHomepath, logger, configFilePath);
                                                    }
                                                }
                                            }

                                            logger.info("CSV FIle generated");
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.info("Username or password was changed in config file");
                                    System.out.println(
                                            "Username or password or Path to save CSV was changed, Please initilize the config file once again");
                                }
                                return;
                            } else {
                                logger.info("config file does not exist");
                                System.out.println("File does not exist");
                                return;
                            }
                        } catch (IOException e) {
                            logger.info("Issue with getting file");
                            e.printStackTrace();
                        }

                    }

                }
                TempCounterforThrowingerror++;


                //TempCounterforThrowingerror++;
                //	System.out.println("Arguments are not in correct combination please try --help or -h for getting correct combination");


            } catch (ParseException exception) {
                TempCounterforThrowingerror++;
                System.out.print("Parse error: ");
                System.out.println(exception.getMessage());

            }
        }

        if (TempCounterforThrowingerror == 0) {
            System.out.println(
                    "Arguments are not in correct combination please try --help or -h for getting correct combination");
        }

    }
}
