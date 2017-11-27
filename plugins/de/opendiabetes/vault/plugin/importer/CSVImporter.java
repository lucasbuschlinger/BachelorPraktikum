package de.opendiabetes.vault.plugin.importer;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.validator.CsvValidator;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class CSVImporter extends FileImporter {
    protected final CsvValidator validator;
    protected char delimiter;

    public CSVImporter(String importFilePath, CsvValidator validator, char delimiter) {
        super(importFilePath);
        this.validator = validator;
        this.delimiter = delimiter;
    }

    protected boolean processImport(InputStream fis, String filenameForLogging) {
        importedData = new ArrayList<>();
        importedRawData = new ArrayList<>();
        List<String[]> metaEntrys = new ArrayList<>();

        CsvReader creader = null;
        try {
            // open file
            creader = new CsvReader(fis, delimiter, Charset.forName("UTF-8"));

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

    protected abstract List<VaultEntry> parseEntry(CsvReader creader) throws Exception;

    protected abstract void preprocessingIfNeeded(String filePath);
}
