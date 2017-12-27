package de.opendiabetes.vault.plugin.importer.crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Actual class for crawling the minimed web page.
 */
public class CrawlerClass {

    /**
     * Time until the crawler should try to finish the action before timing out.
     */
    private static final int CONNECT_TIMEOUT = 60000;

    /**
     * The user agent when crawling the web page.
     */
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/56.0.2924.87 Safari/537.36";

    /**
     * Checks if the login and the entered dates are and then downloads the csv file.
     *
     * @param loginCookies - the login cookies to be sent.
     * @param startDate - start date from when the csv file data should begin.
     * @param endDate - end date until when the csv file data should go.
     * @param userWorkingDirecotry - the director where the csv file should be downloaded to.
     * @param logger - a logger instance.
     * @param configFilePath - file path to specific crawler configuration.
     */
    private void generateDocument(final Map<String, String> loginCookies,
                                  final String startDate,
                                  final String endDate,
                                  final String userWorkingDirecotry,
                                  final Logger logger,
                                  final String configFilePath) {

        logger.info("Inside class CrawlerClass");
        try {
            Connection.Response reportDocument = Jsoup
                    .connect("https://carelink.minimed.eu/patient/main/selectCSV.do?t=11?t=11?t=11?t=11").timeout(CONNECT_TIMEOUT)
                    /* .ignoreContentType(false).userAgent(userAgent)*/.cookies(loginCookies)
                    /* .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,;q=0.8")
					.header("Content-length", "101")
					.data("org.apache.struts.taglib.html.TOKEN", "53f325519c62adcec1a3128908017474")*/
                    .data("report", "11").data("listSeparator", ",")
                    //.data("customerID","50577452") // customer Id can be
                    // optional.
                    .data("datePicker2", startDate) // start date
                    .data("datePicker1", endDate) // End date
					/*.header("X-Requested-With", "XMLHttpRequest")*/.method(Connection.Method.GET).execute();

            String userHome = userWorkingDirecotry;
            String outputFolder = userHome + File.separator + "careLink-Export";

            PrintWriter pw1 = new PrintWriter(new File(outputFolder + (new Date().getTime()) + ".csv"));
            pw1.write(reportDocument.body());
            pw1.close();
            System.out.println("Export Sucessfull!");
            System.out.println("File will be saved to location " + userHome + " with name: " + "\"careLink-Export"
                    + (new Date().getTime()) + ".csv\"");
            logger.info("Export Sucessfull!");

        } catch (IOException e) {
            logger.info("There is an issue Downloading File. Please try again after some time!!");
            System.out.println(
                    "There is an issue Downloading File. Please try checking output path or try again after some time!!");

        }

    }

}
