package de.opendiabetes.vault.plugin.importer.validator;

import com.csvreader.CsvReader;

import java.io.IOException;

public class LibreTextCSVValidator extends CSVValidator {
    //TODO javadoc
    public static final String LIBRE_HEADER_DE_SCAN     = "Gescannte Glukose (mg/dL)";
    public static final String LIBRE_HEADER_DE_HISTORIC =  "Historische Glukose (mg/dL)";
    public static final String LIBRE_HEADER_DE_BLOOD    = "Teststreifen-Blutzucker (mg/dL)";
    public static final String LIBRE_HEADER_DE_TIME     = "Uhrzeit";
    public static final String TIME_FORMAT_LIBRE_DE    = "yyyy.MM.dd HH:mm";
    public static final String[] LIBRE_HEADER_DE= {
            LIBRE_HEADER_DE_SCAN,
            LIBRE_HEADER_DE_HISTORIC,
            LIBRE_HEADER_DE_BLOOD,
            LIBRE_HEADER_DE_TIME,
            TIME_FORMAT_LIBRE_DE
    };

    /**
     * Constructor.
     */
    public LibreTextCSVValidator() {
        super(LIBRE_HEADER_DE, LIBRE_HEADER_DE);
    }

}
