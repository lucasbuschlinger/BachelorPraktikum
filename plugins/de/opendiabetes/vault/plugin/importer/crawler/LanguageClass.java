package de.opendiabetes.vault.plugin.importer.crawler;

import java.util.logging.Logger;

import java.awt.event.KeyEvent;

public class LanguageClass {

    public int getReplacment(String lang, Logger logger) {
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
