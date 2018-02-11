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
package de.opendiabetes.vault.plugin.importer.ODV;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.importer.AbstractImporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import sun.rmi.runtime.Log;

import javax.swing.text.html.HTMLDocument;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Wrapper class for the {@link ODVImporterImplementation} used by the {@link org.pf4j.PluginManager}.
 *
 * @author Lucas Buschlinger
 */
public class ODVImporter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public ODVImporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the Medtronic importer plugin.
     */
    @Extension
    public static class ODVImporterImplementation extends AbstractImporter {

        /**
         *
         */
        private String checksumFileIdentifier = "-checksum.txt";
        /**
         *
         */
        private final int bufferSize = 1024;
        /**
         *
         */
        private String tempDir = "temp";
        /**
         *
         */
        private String metaFile = "meta.info";
        /**
         *
         */
        private String importFilePath;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getImportFilePath() {
            return importFilePath;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setImportFilePath(final String filePath) {
            this.importFilePath = filePath;
        }

        /**
         *
         * @return
         */
        @Override
        public boolean importData() {
            Map<String, List<String>> metaInfo;
            Map<String, String> unimportedFiles = new HashMap<>();
            importedData = new ArrayList<>();
            String reasonNoPlugin = "No applicable importer plugin available for this file";
            String reasonChecksumFailed = "The integrity of the data could not be verified via the checksum";
            try {
                unzipArchive(importFilePath, tempDir);
            } catch (IOException exception) {
                LOG.log(Level.SEVERE, "Error while unzipping archive: " + importFilePath);
                return false;
            }
            try {
                 metaInfo = readMetaFile(tempDir + File.separator + metaFile);
            } catch (IOException exception) {
                LOG.log(Level.SEVERE, "Error while reading meta file");
                return false;
            }
            Iterator iterator = metaInfo.entrySet().iterator();
            PluginManager manager = new DefaultPluginManager();
            manager.loadPlugins();
            manager.startPlugins();
            while (iterator.hasNext()){
                Map.Entry metaEntry = (Map.Entry) iterator.next();
                String plugin = (String) metaEntry.getKey();
                ArrayList<String> furtherEntries = (ArrayList<String>) metaEntry.getValue();
                String inputFilepath = tempDir + File.separator + furtherEntries.get(0);
                String fileChecksum = furtherEntries.get(1);
                Importer importer;
                try {
                    importer = (Importer) manager.getExtensions(plugin).get(0);
                } catch (Exception exception) {
                    LOG.log(Level.WARNING, "No applicable Plugin available for " + plugin);
                    unimportedFiles.put(inputFilepath, reasonNoPlugin);
                    continue;
                }
                if (!verifyChecksum(inputFilepath, fileChecksum)) {
                    unimportedFiles.put(inputFilepath, reasonChecksumFailed);
                    continue;
                }
                importer.setImportFilePath(inputFilepath);
                importer.importData();
                importedData.addAll(importer.getImportedData());
            }
            return true;
        }

        /**
         *
         * @param configuration the configuration object
         * @return
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            return false;
        }

        /**
         *
         * @param filePath Path to the import file. Used to derive the Path to the file containing the checksum.
         * @param checksum bla
         */
        public boolean verifyChecksum(final String filePath, final String checksum) {
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(filePath);
            } catch (FileNotFoundException exception) {
                LOG.log(Level.WARNING, "Could not find file, integrity could not be verified: " + filePath);
                return false;
            }
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException exception) {
                LOG.log(Level.WARNING, "Could not verify integrity, SHA-512 algorithm not available");
                return false;
            }
            byte[] dataBytes = new byte[bufferSize];
            int nextRead = 0;
            try {
                while ((nextRead = fileInputStream.read(dataBytes)) != -1) {
                    digest.update(dataBytes, 0, nextRead);
                }
            } catch (IOException exception) {
                LOG.log(Level.WARNING, "Could not read file, integrity not verified");
                return false;
            }

            String generateChecksum = (new HexBinaryAdapter()).marshal(digest.digest());
            if (!generateChecksum.equalsIgnoreCase(checksum)) {
                LOG.log(Level.SEVERE, "Checksum is not valid for the specified file");
                return false;
            } else {
                LOG.log(Level.INFO, "Checksum successfully verified");
            }
            try {
                fileInputStream.close();
            } catch (IOException exception) {
                LOG.log(Level.WARNING, "Could not close stream to file");
            }
            return true;
        }

        /**
         *
         * @param zipFile bla
         * @param outputDirectory bla
         * @throws IOException bla
         */
        private void unzipArchive(final String zipFile, final String outputDirectory) throws IOException {
            File outputFolder = new File(outputDirectory);
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[bufferSize];
            while (entry != null) {
                String fileName = entry.getName();
                File extractedFile = new File(outputDirectory + File.separator + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(extractedFile);

                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();
                entry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
            LOG.log(Level.INFO, "Successfully unzipped archive " + zipFile);
        }

        /**
         *
         * @param metaFile bla
         * @return bla
         * @throws IOException bla
         */
        private Map<String, List<String>> readMetaFile(final String metaFile) throws IOException {
            String line;
            Map<String, List<String>> result = new HashMap<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(metaFile));
            line = bufferedReader.readLine();
            while (line != null) {
                String[] lineEntries = line.split("=");
                String importer = lineEntries[0];
                List<String> fileAndChecksum = new ArrayList<>();
                fileAndChecksum.add(lineEntries[1]);
                fileAndChecksum.add(lineEntries[2]);
                result.put(importer, fileAndChecksum);
                line = bufferedReader.readLine();
            }
            return result;
        }

    }

}
