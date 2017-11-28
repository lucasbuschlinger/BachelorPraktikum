/**
 * Copyright (C) 2017 OpenDiabetes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.validator.CSVValidator;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the functionality for importing CSV based data.
 */
public abstract class CSVImporter extends FileImporter {
    /**
     * The validator who handles CSV data.
     */
    protected final CSVValidator validator;
    /**
     * Delimiter used in the CSV file.
     */
    protected char delimiter;

    /**
     * Default Constructor.
     * TODO: deprecated?
     */
    public CSVImporter() {
        this.validator = null;
    }

    /**
     * Constructor for a CSV validator.
     * @param importFilePath Path to the import file.
     * @param validator The validator to use.
     * @param delimiter The delimiter used in the CSV file.
     */
    public CSVImporter(final String importFilePath, final CSVValidator validator, final char delimiter) {
        super(importFilePath);
        this.validator = validator;
        this.delimiter = delimiter;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean processImport(final InputStream fileInputStream, final String filenameForLogging) {
        importedData = new ArrayList<>();
        importedRawData = new ArrayList<>();
        List<String[]> metaEntrys = new ArrayList<>();

        CsvReader creader = null;
        try {
            // open file
            creader = new CsvReader(fileInputStream, delimiter, Charset.forName("UTF-8"));

            //validate header
            do {
                if (!creader.readHeaders()) {
                    // no more lines --> no valid header
                    LOG.log(Level.WARNING, "No valid header found in File:{0}", filenameForLogging);
                    return false;
                }
                metaEntrys.add(creader.getHeaders());
            } while (!validator.validateHeader(creader.getHeaders()));
            metaEntrys.remove(metaEntrys.size() - 1); //remove valid header

            // read entries
            while (creader.readRecord()) {
                List<VaultEntry> entryList = parseEntry(creader);
                boolean parsed = false;
                if (entryList != null && !entryList.isEmpty()) {
                    for (VaultEntry item : entryList) {
                        item.setRawId(importedRawData.size()); // add array position as raw id
                        importedData.add(item);
                        LOG.log(Level.FINE, "Got Entry: {0}", entryList.toString());
                    }
                    parsed = true;
                }
                importedRawData.add(new RawEntry(creader.getRawRecord(), parsed));
                LOG.log(Level.FINER, "Put Raw: {0}", creader.getRawRecord());
            }

        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error while parsing CSV: "
                    + filenameForLogging, ex);
        }
        return true;
    }

    /**
     * Method to parse entries in a file.
     * @param csvReader Reader for CSV files.
     * @return List of VaultEntry holding the imported data.
     * @throws Exception If a entry could not be parsed.
     */
    protected abstract List<VaultEntry> parseEntry(CsvReader csvReader) throws Exception;

    /**
     * {@inheritDoc}
     */
    protected abstract void preprocessingIfNeeded(String filePath);
}
