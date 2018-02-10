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
package de.opendiabetes.vault.plugin.exporter.ODVExporter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.util.FileUtils;
import sun.rmi.runtime.Log;

import javax.sound.sampled.LineEvent;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
import java.util.zip.ZipOutputStream;

/**
 * Wrapper class for the VaultCSVExporter plugin.
 *
 * @author Lucas Buschlinger
 */
public class ODVExporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public ODVExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the ODVExporter plugin.
     */
    @Extension
    public static final class ODVExporterImplementation implements Exporter {

        /**
         * This path to the file where the ZIP should go.
         */
        private String exportFilePath;
        /**
         * The database to export data from.
         */
        private VaultDao database;
        /**
         * List holding all StatusListeners registered to the exporter.
         */
        private List<Exporter.StatusListener> listeners = new ArrayList<>();
        /**
         * The properties which will get passed on to the exporters.
         */
        private Properties config;
        /**
         * The size of the buffers used to write to the files.
         */
        private final int bufferSize = 1024;
        /**
         * The temporary directory to use.
         */
        private final String tempDir = "temp/";

        /**
         * {@inheritDoc}
         */
        @Override
        public void setExportFilePath(final String filePath) {
            exportFilePath = filePath;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getExportFilePath() {
            return exportFilePath;
        }

        /**
         * This setter sets the database to export data from.
         * The database is used by several exporters.
         *
         * @param object The database to be set.
         * @throws IllegalArgumentException Thrown if something different than a {@link de.opendiabetes.vault.data.VaultDao}
         *                                  is supplied.
         */
        @Override
        public void setAdditional(final Object object) throws IllegalArgumentException {
            if (object instanceof VaultDao) {
                database = (VaultDao) object;
            } else {
                throw new IllegalArgumentException("Can not set " + object.getClass().getName()
                        + ", only VaultDao objects are applicable!");
            }
        }

        @Override
        public int exportDataToFile(final List<VaultEntry> data) {
            FileOutputStream fileOutputStream;
            ZipOutputStream zipOutputStream;
            Map<String, String> exportFiles = new HashMap<>();
            try {
                fileOutputStream = new FileOutputStream(exportFilePath);
            } catch (FileNotFoundException exception) {
                LOG.log(Level.SEVERE, "Could not create file at " + exportFilePath);
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            zipOutputStream = new ZipOutputStream(fileOutputStream, Charset.forName("UTF-8"));

            try {
                File file = new File(tempDir); //tempDir + "/ODVExporter2");
                file.mkdir();
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, "Could not create temporary folder");
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }

            PluginManager manager = new DefaultPluginManager();
            manager.loadPlugins();
            manager.startPlugins();

            List<Exporter> exporters = manager.getExtensions(Exporter.class);

            for (Exporter exporter : exporters) {
                String name = exporter.getClass().getName().replaceAll(".*\\$", "")
                        .replace("Implementation", "");
                if (name.contains("ODVExporter") || exportFiles.containsKey("name")) {
                    continue;
                }
                try {
                    exporter.setAdditional(database);
                } catch (Exception ex) {
                    LOG.log(Level.INFO, "Skipping exporter " + name + " as it does not export from the database");
                    continue;
                }

                String exportFile = tempDir + name + ".export";
                exporter.setExportFilePath(exportFile);
                exporter.loadConfiguration(config);
                /*
                exporter.registerStatusCallback(new StatusListener() {
                    @Override
                    public void onStatusCallback(final int progress, final String status) {
                        notifyStatus(progress, name + ": " + status);
                    }
                });
                */
                try {
                    exporter.exportDataToFile(null);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Could not export with exporter: " + name + "Ex: " + ex);
                    // return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
                }
                exportFiles.put(name, exportFile);
            }
            String metaFile;
            try {
                metaFile = makeMetaFile(exportFiles);
            } catch (IOException exception) {
                LOG.log(Level.SEVERE, "Couldn't generate meta file for ZIP-archive");
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            // Adding all generated export files and the meta fil to the zip
            try {
                Iterator iterator = exportFiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    addFileToZip(pair.getValue().toString(), zipOutputStream);
                }
                addFileToZip(metaFile, zipOutputStream);
            } catch (Exception exception) {
                    LOG.log(Level.SEVERE, "Could not add file to ZIP " + exception);
                    return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            try {
                zipOutputStream.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Couldn't close zipOutputStream " + ex);
            }
            try {
                makeChecksum(exportFilePath);
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, "Couldn't generate checksum for ZIP-archive");
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            return 0;
        }

        /**
         * This method generate the checksum in form of an SHA-512 hash for the created ZIP-archive.
         *
         * @param fileName The name of the ZIP to generate the checksum for
         * @throws IOException Thrown if the streams could not be opened for reading/writing the files.
         * @throws NoSuchAlgorithmException Thrown if the SHA-512 hash is not available on the system.
         */
        private void makeChecksum(final String fileName) throws IOException, NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            FileInputStream inputStream = new FileInputStream(fileName);
            FileOutputStream outputStream = new FileOutputStream(fileName + "-checksum.txt");

            byte[] dataBytes = new byte[bufferSize];

            int nextRead = 0;
            while ((nextRead = inputStream.read(dataBytes)) != -1) {
                digest.update(dataBytes, 0, nextRead);
            }

            String checksum = (new HexBinaryAdapter()).marshal(digest.digest());
            // Writing the hex-digest to the file and closing the streams
            outputStream.write(checksum.getBytes());
            inputStream.close();
            outputStream.close();
        }

        /**
         * This adds the named file to the given {@link ZipOutputStream} as a {@link ZipEntry}.
         *
         * @param file The name of the file to be added.
         * @param zipStream The zip to add the file to.
         * @throws IOException Thrown if the file could not be added to the zip.
         */
        private void addFileToZip(final String file, final ZipOutputStream zipStream) throws IOException {
            byte[] buffer = new byte[bufferSize];
            ZipEntry zipEntry = new ZipEntry(file.replace("temp/", ""));
            zipStream.putNextEntry(zipEntry);
            File zipFile = new File(file);
            FileInputStream in = new FileInputStream(zipFile);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zipStream.write(buffer, 0, len);
            }
            in.close();
            zipStream.closeEntry();
        }



        /**
         * This method generates the meta file for the ZIP-archive.
         * In contains which files where generated by exporters.
         *
         * @param fileList The list of exported files generated by the available export plugins.
         * @return The name of the generated meta file.
         * @throws IOException Thrown if the file could not be created.
         */
        private String makeMetaFile(final Map<String, String> fileList) throws IOException {
            final String metaFile = tempDir + "meta.info";
            FileOutputStream outputStream = new FileOutputStream(metaFile);
            Iterator iterator = fileList.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                String exporter = pair.getKey().toString();
                String file = pair.getValue().toString().replace("temp/", "");
                String line = exporter + "=" + file + "\n";
                outputStream.write(line.getBytes());
            }
            outputStream.close();
            return  metaFile;
        }

        /**
         * This does not really load the configuration in some way but rather stores it to pass it on to the called exporters.
         *
         * @param configuration The configuration object.
         * @return True if the configuration has been stored.
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            config = configuration;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void registerStatusCallback(final StatusListener listener) {
            this.listeners.add(listener);
        }

        /**
         * Notifies the registered {@link de.opendiabetes.vault.plugin.exporter.Exporter.StatusListener}s about progress.
         * See {@link de.opendiabetes.vault.plugin.exporter.Exporter.StatusListener#onStatusCallback} to register listeners.
         *
         * @param progress Percentage of completion.
         * @param status Current status.
         */
        private void notifyStatus(final int progress, final String status) {
            listeners.forEach(listener -> listener.onStatusCallback(progress, status));
        }
    }
}
