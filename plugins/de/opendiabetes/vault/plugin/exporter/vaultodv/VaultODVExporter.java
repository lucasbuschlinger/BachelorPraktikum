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
package de.opendiabetes.vault.plugin.exporter.vaultodv;

import com.csvreader.CsvWriter;
import de.opendiabetes.vault.container.csv.CsvEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.container.csv.VaultCsvEntry;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Not implemented yet.
 * Only a dummy class for the importer.
 * TO_DO might be superseded by the ODVExporter, will have to discuss with Jens about removal
 */
public class VaultODVExporter extends Plugin {

    /**
     * Name of the data file inside the ZIP archive.
     * Also used for recognition by the {@link de.opendiabetes.vault.plugin.importer.vaultodv.VaultODVImporter}.
     */
    public static final String DATA_ZIP_ENTRY = "data.csv";
    /**
     * Name of the file containing the signature hash inside the ZIP archive.
     * Also used for recognition by the {@link de.opendiabetes.vault.plugin.importer.vaultodv.VaultODVImporter}.
     */
    public static final String SIGNATURE_ZIP_ENTRY = "sig.txt";
    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public VaultODVExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the VaultODV exporter plugin.
     */
    @Extension
    public static class VaultODVExporterImplementation extends VaultExporter {

        /**
         * Writes the data to a CSV file and generates a signature hash.
         * Then it puts both of this into a ZIP archive file.
         *
         * @param csvEntries The {@link ExportEntry} to be exported.
         * @throws IOException Thrown if the SHA-512 hash algorithm is missing.
         */
        @Override
        protected void writeToFile(final List<ExportEntry> csvEntries) throws IOException {
            // Setup compression stuff
            FileOutputStream fileOutputStream = getFileOutputStream();
            final int zipCompressionLevel = 9;

            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            zipOutputStream.setLevel(zipCompressionLevel);

            // Setup signature
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException exception) {
                LOG.log(Level.SEVERE, "Missing hash algorithm for signature. No file exported!", exception);
                throw new IOException("Missing hash algorithm for signature.");
            }
            DigestOutputStream digestOutputStream = new DigestOutputStream(zipOutputStream, digest);

            // write data
            zipOutputStream.putNextEntry(new ZipEntry(DATA_ZIP_ENTRY));
            CsvWriter cwriter = new CsvWriter(digestOutputStream, VaultCsvEntry.CSV_DELIMITER,
                    Charset.forName("UTF-8"));

            cwriter.writeRecord(((CsvEntry) csvEntries.get(0)).getCsvHeaderRecord());
            for (ExportEntry item : csvEntries) {
                cwriter.writeRecord(((CsvEntry) item).toCsvRecord());
            }
            cwriter.flush();

            // add signature file
            zipOutputStream.putNextEntry(new ZipEntry(SIGNATURE_ZIP_ENTRY));
            String sigString = (new HexBinaryAdapter()).marshal(digest.digest());
            zipOutputStream.write(sigString.getBytes("UTF-8"), 0, sigString.getBytes("UTF-8").length);

            // Closing the streams
            cwriter.close();
            digestOutputStream.close();
            zipOutputStream.close();
            fileOutputStream.close();
        }

    }

}
