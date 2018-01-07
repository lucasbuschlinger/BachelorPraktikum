package de.opendiabetes.vault.plugin.importer.crawler;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Class for custom log formatting.
 */
public class CrawlerLogFormatter extends Formatter {

    @Override
    public String format(final LogRecord record) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append(record.getMessage());
        sb.append("\n");
        return sb.toString();
    }
}
