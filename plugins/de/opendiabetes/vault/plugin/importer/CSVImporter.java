/*
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.validator.CSVValidator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * This class implements the functionality for importing CSV based data.
 */
public abstract class CSVImporter extends FileImporter {
    /**
     * The validator who handles CSV data.
     */
    private CSVValidator validator;
    /**
     * Delimiter used in the CSV file.
     */
    private char delimiter = 0; //set delimiter to "null" to indicate that it is not valid yet


    /**
     * Constructor for a CSV validator. (automatic delimiter detection).
     *
     * @param csvValidator The validator to use.
     */
    public CSVImporter(final CSVValidator csvValidator) {
        this.validator = csvValidator;
    }

    /**
     * Method used to detect the valid delimiter by trying to validate the header using a given delimiter.
     *
     * @param delimiter       the delimiter to use to read the file
     * @param fileInputStream the file to read
     * @param metaEntries     placeholder for future extensions
     * @return a CsvReader pointing to the headers, null if the headers could not be validated
     * @throws IOException if file reading goes wrong
     */
    private CsvReader getValidatedCreader(final char delimiter, final InputStream fileInputStream, final List<String[]> metaEntries)
            throws IOException {
        // open file
        CsvReader creader = new CsvReader(fileInputStream, delimiter, Charset.forName("UTF-8"));

        //validate header
        do {
            if (!creader.readHeaders()) {
                LOG.log(Level.FINEST, "automatic delimiter detection detected invalid delimiter: " + delimiter);
                metaEntries.clear();
                return null;
            }
            metaEntries.add(creader.getHeaders());
        } while (!validator.validateHeader(creader.getHeaders()));
        metaEntries.remove(metaEntries.size() - 1); //remove valid header
        LOG.log(Level.INFO, "automatic delimiter detection detected valid delimiter: " + delimiter);
        return creader;
    }

    /**
     * {@inheritDoc}
     */
    public boolean processImport(final InputStream fileInputStream, final String filenameForLogging) {
        importedData = new ArrayList<>();
        importedRawData = new ArrayList<>();
        final int maxProgress = 100;

        //This list is used as a placeholder for future extensions
         List<String[]> metaEntries = new ArrayList<>();

        this.notifyStatus(0, "Reading Header");
        try {
            CsvReader creader = null;
            if (getDelimiter() == 0) { //try to detect the delimiter by trial and error
                LOG.log(Level.INFO, "using automatic delimiter detection");
                char[] delimiterList = {',', ';', '\t'};
                for (char delimiter : delimiterList) {
                    creader = getValidatedCreader(delimiter, fileInputStream, metaEntries);
                    if (null != creader) {
                        setDelimiter(delimiter);
                        break;
                    }
                }
            } else { // use the delimiter that was set
                creader = getValidatedCreader(getDelimiter(), fileInputStream, metaEntries);
            }
            if (creader == null) { //header could not be validated
                LOG.log(Level.WARNING, "No valid header found in File:{0}", filenameForLogging);
                return false;
            }
            // read entries
            while (creader.readRecord()) {
                /*here the method template is used to process all records */
                List<VaultEntry> entryList = parseEntry(creader);

                boolean entryIsInterpreted = false;
                if (entryList != null && !entryList.isEmpty()) {
                    for (VaultEntry item : entryList) {
                        item.setRawId(importedRawData.size()); // add array position as raw id
                        importedData.add(item);
                        LOG.log(Level.FINE, "Got Entry: {0}", entryList.toString());
                    }
                    entryIsInterpreted = true;
                }
                importedRawData.add(new RawEntry(creader.getRawRecord(), entryIsInterpreted));
                LOG.log(Level.FINER, "Put Raw: {0}", creader.getRawRecord());
            }
            this.notifyStatus(maxProgress, "Done importing all entries");

        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error while parsing CSV: "
                    + filenameForLogging, ex);
        }
        return true;
    }

    /**
     * Method to parse entries in a file.
     *
     * @param csvReader Reader for CSV files.
     * @return List of VaultEntry holding the imported data.
     * @throws Exception If a entry could not be parsed.
     */
    protected abstract List<VaultEntry> parseEntry(CsvReader csvReader) throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract void preprocessingIfNeeded(String filePath);

    /**
     * Getter for the validator.
     *
     * @return The validator.
     */
    protected CSVValidator getValidator() {
        return validator;
    }

    /**
     * Setter for the validator.
     *
     * @param csvValidator The validator to set.
     */
    private void setValidator(final CSVValidator csvValidator) {
        this.validator = csvValidator;
    }

    /**
     * Getter for the delimiter.
     *
     * @return The delimiter.
     */
    protected char getDelimiter() {
        return delimiter;
    }

    /**
     * Setter for the delimiter.
     *
     * @param csvDelimiter The delimiter to set.
     */
    protected void setDelimiter(final char csvDelimiter) {
        this.delimiter = csvDelimiter;
    }

    /**
     * Allows to set the delimiter to use.
     * {@inheritDoc}
     */
    @Override
    public boolean loadConfiguration(final Properties configuration) {
        if (configuration.containsKey("delimiter")) {
            String delimiter = configuration.getProperty("delimiter");
            if (delimiter != null && delimiter.length() == 1) {
                this.setDelimiter(delimiter.charAt(0));
                return true;
            } else {
                LOG.log(Level.WARNING,
                        "Please set the delimiter property in the following format: \n"
                                + "delimiter = ,\n"
                                + "where \",\" can be any char you want to use as delimiter\n"
                                + "or remove the delimiter property to use automatic delimiter detection");
                return false;
            }

        }
        return true;
    }
}
