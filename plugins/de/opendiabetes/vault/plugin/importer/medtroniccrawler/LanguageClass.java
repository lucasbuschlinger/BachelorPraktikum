package de.opendiabetes.vault.plugin.importer.medtroniccrawler;

import java.util.logging.Logger;

import java.awt.event.KeyEvent;

/**
 * Helper class for localisation conditions.
 */
class LanguageClass {

    /**
     * Returns the correct key event for the given language string. If the language could not be detected the function will return 0.
     *
     * @param lang - a two char language string.
     * @param logger - a logger instance.
     * @return a key event.
     */
    int getReplacment(final String lang, final Logger logger) {
        // This function is used when pressing keys.
        logger.info("Inside Class, LanguageClass");
        if (lang.contains("en")) {
            logger.info("Inside Class LanguageClass, English language");
            return KeyEvent.VK_N;
        } else if (lang.contains("de")) {
            logger.info("Inside Class LanguageClass, Deutsch language");
            return KeyEvent.VK_W;
        } else {
            return 0;
        }
    }

}
