package de.opendiabetes.vault.plugin.importer.crawler;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyCustomFormatterForLogger extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append(record.getMessage());
        sb.append("\n");
        return sb.toString();
    }
}
