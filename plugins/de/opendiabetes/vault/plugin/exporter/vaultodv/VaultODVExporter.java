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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Boolean.parseBoolean;

/**
 * Not implemented yet.
 * Only a dummy class for the importer.
 */
public class VaultODVExporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public VaultODVExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

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
     * Actual implementation of the VaultODV exporter plugin.
     */
    @Extension
    public static final class VaultODVExporterImplementation extends VaultExporter {

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

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            // Status update constant
            final int loadConfigProgress = 0;
            // Format of dates which must be used.
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            this.notifyStatus(loadConfigProgress, "Loading configuration");

            if (!configuration.containsKey("periodRestriction")
                    || configuration.getProperty("periodRestriction") == null
                    || configuration.getProperty("periodRestriction").length() == 0) {
                LOG.log(Level.WARNING, "VaultODVExporter configuration does not specify whether the data is period restricted");
                return false;
            }
            boolean restriction = parseBoolean(configuration.getProperty("periodRestriction"));
            this.setIsPeriodRestricted(restriction);

            // Only necessary to look for dates if data is period restricted
            if (restriction) {
                Date dateFrom;
                Date dateTo;
                String startDate = configuration.getProperty("periodRestrictionFrom");
                String endDate = configuration.getProperty("periodRestrictionTo");
                if (startDate == null || endDate == null) {
                    LOG.log(Level.SEVERE, "VaultODVExporter configuration specified a period restriction on the data but no correct"
                            + " dates were specified.");
                    return false;
                }
                // Parsing to actual dates
                try {
                    dateFrom = dateFormat.parse(startDate);
                    dateTo = dateFormat.parse(endDate);
                } catch (ParseException exception) {
                    LOG.log(Level.SEVERE, "Either of the dates specified in the VaultODVExporter config is malformed."
                            + " The expected format is dd/mm/yyyy.");
                    return false;
                }

                // Check whether the start time lies before the end time
                if (dateFrom.after(dateTo)) {
                    LOG.log(Level.WARNING, "The date the data is period restricted from lies after the date it is restricted to,"
                            + " check order.");
                    return false;
                }

                this.setExportPeriodFrom(dateFrom);
                this.setExportPeriodTo(dateTo);
                LOG.log(Level.INFO, "Data is period restricted from " + dateFrom.toString() + " to " + dateTo.toString());
                return true;
            } else {
                LOG.log(Level.INFO, "Export data is not period restricted by VaultODVExporter configuration.");
                return true;
            }
        }

        /**
         * {@inheritDoc}
         */
        public String getHelp(){
            //TODO write help
            return null;
        }
    }

}
