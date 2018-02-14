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
package de.opendiabetes.vault.plugin.importer.vaultodv;


import com.csvreader.CsvReader;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.exporter.vaultodv.VaultODVExporter;
import de.opendiabetes.vault.plugin.importer.CSVImporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Wrapper class for the VaultODVImporter plugin.
 *
 * @author Gia Han Tina Lu
 */
public class VaultODVImporter extends Plugin {


    /**
     * Constructor for PluginMangager.
     *
     * @param wrapper the PluginWrapper
     *
     */
    public VaultODVImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the VaultODV importer plugin.
     */
    @Extension
    public static final class VaultODVImporterImplementation extends CSVImporter {
        /**
         * Constructor for a CSV validator.
         *
         */
        public VaultODVImporterImplementation() {
            super(new VaultCSVValidator());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean importData() {
            String importPath = getImportFilePath();
            preprocessingIfNeeded(importPath);

            try {
                // open zip package
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(importPath));

                // Setup signature stuff
                MessageDigest messageDigest;
                try {
                    messageDigest = MessageDigest.getInstance("SHA-512");
                } catch (NoSuchAlgorithmException ex) {
                    LOG.log(Level.SEVERE, "Missing hash algorithm for signature. No file exported!", ex);
                    return false;
                }
                DigestInputStream digestInputStream = new DigestInputStream(zipInputStream, messageDigest);

                ZipEntry tmpEntry;
                String signature = null;
                boolean processingResult = false;
                while ((tmpEntry = zipInputStream.getNextEntry()) != null) {
                    switch (tmpEntry.getName()) {
                        case VaultODVExporter.DATA_ZIP_ENTRY:
                            // process data while creating signature
                            processingResult = super.processImport(digestInputStream, importPath);
                            break;
                        case VaultODVExporter.SIGNATURE_ZIP_ENTRY:
                            // read signature as string
                            BufferedReader bufReader = new BufferedReader(new InputStreamReader(zipInputStream, "UTF-8"));
                            signature = bufReader.lines().collect(Collectors.joining("\n"));
                            bufReader.close();
                            break;
                        default:
                            LOG.warning("Found unexpected entry: " + tmpEntry.getName());
                            break;
                    }
                }
                zipInputStream.close();

                if (processingResult) { // if not, data entry is missing
                    // check signature
                    String sigString = (new HexBinaryAdapter()).marshal(messageDigest.digest());
                    if (sigString.equalsIgnoreCase(signature)) {
                        return true;
                    } else {
                        LOG.severe("Signature check failed! File will be dropped.");
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (FileNotFoundException ex) {
                LOG.log(Level.SEVERE, "Error opening a FileInputStream for file: "
                        + importPath, ex);
                return false;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error reading compressed file: "
                        + importPath, ex);
                return false;
            }
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            return super.loadConfiguration(configuration);
        }

        /**
         * Parser for VaultODV Data which throws an UnsupportedOperationException, because not needed.
         *
         * @param csvReader Reader for CSV files.
         * @return Nothing.
         * @throws UnsupportedOperationException ParseEntry is not implemented for VaultODVImporter.
         */
        @Override
        protected List<VaultEntry> parseEntry(final CsvReader csvReader) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("ParseEntry not implemented for VaultODVImporter plugin! (Not needed");
        }

        /**
         * Unimplemented preprocessing method as no preprocessing is necessary for VaultODV CSV data.
         *
         * @param filePath Path to the file that would be preprocessed.
         */
        @Override
        protected void preprocessingIfNeeded(final String filePath) { }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getHelpFilePath() {
            //TODO write help
            return null;
        }
    }
}
