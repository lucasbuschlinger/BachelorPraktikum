package de.opendiabetes.vault.plugin.importer.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Handles the login detail page.
 */
public class Authentication {

    /**
     * Holds the login cookies set by the webpage.
     */
    private Map<String, String> loginCookies = new HashMap<String, String>();

    /*/**
     * Useragent that will be sent to the webpage.
     */
    /*public static final String USERAGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";*/

    /**
     * Returns the login cookies set by the webpage.
     *
     * @return the login cookies hashmap.
     */
    Map<String, String> getcookies() {
        return this.loginCookies;

    }

    /**
     * Checks if the given username and password are correct.
     *
     * @param username - the users username.
     * @param password - the users password.
     * @param logger a logger instance.
     * @return a boolean value indicating if the credentials are correct.
     */
    Boolean checkConnection(final String username, final String password, final Logger logger) {
        // check if login credentials are correct or nots
        try {
            logger.info("Inside class checkConnection");

            Connection.Response res = Jsoup.connect("https://carelink.minimed.eu/patient/j_security_check")
                    .data("j_username", username).data("j_password", password).method(Connection.Method.POST).execute();
            loginCookies = res.cookies();
            System.out.println("correct Username and Password");
            logger.info("correct Username and Password");
            return true;
        } catch (Exception e) {
            logger.info("Incorrect Username or Password");
            System.out.println("Incorrect Username or Password");
            return false;
        }

    }

    /**
     * Returns language string extracted from the cookies. If the language could not be detected the function will return null.
     *
     * @return a two character language string.
     */
    String getLanguage() {
        if (loginCookies.toString().contains("locale=de")) {
            return "de";
        } else if (loginCookies.toString().contains("locale=en")) {
            return "en";
        }
        return null;
    }
}
