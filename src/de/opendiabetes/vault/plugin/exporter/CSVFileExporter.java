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
package de.opendiabetes.vault.plugin.exporter;

import com.csvreader.CsvWriter;

import de.opendiabetes.vault.container.csv.CSVEntry;
import de.opendiabetes.vault.container.csv.VaultCSVEntry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * This defines a standard implementation for writing CSV data.
 *
 * @author Lucas Buschlinger
 * @param <T> Type of the list entries passed from {@link #prepareData(List)} to {@link #writeToFile(String, List)}
 * @param <U> Type of data accepted to export
 */
public abstract class CSVFileExporter<T, U> extends FileExporter<T, U> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeToFile(final String filePath, final List<T> csvEntries) throws IOException {
        FileOutputStream fileOutputStream = getFileOutputStream();
        CsvWriter cwriter = new CsvWriter(fileOutputStream, VaultCSVEntry.CSV_DELIMITER, Charset.forName("UTF-8"));

        cwriter.writeRecord(((CSVEntry) csvEntries.get(0)).getCSVHeaderRecord());
        for (T item : csvEntries) {
            cwriter.writeRecord(((CSVEntry) item).toCSVRecord());
        }
        cwriter.flush();
        cwriter.close();
    }
}
