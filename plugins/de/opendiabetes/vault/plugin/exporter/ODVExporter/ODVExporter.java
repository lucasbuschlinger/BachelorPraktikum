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
import de.opendiabetes.vault.plugin.exporter.Exporter;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Wrapper class for the ODVExporter plugin.
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
         * The location where the ZIP will be exported to.
         */
        private String exportFilePath;
        /**
         * List holding all StatusListeners registered to the exporter.
         */
        private final List<Exporter.StatusListener> listeners = new ArrayList<>();
        /**
         * List containing all compatible plugins that are listed in this plugins config.
         * The data of this field is returned by {@link this#getListOfCompatiblePluginIDs()}
         * and used to list compatibilities among plugins.
         */
        private final List<String> compatiblePlugins = new ArrayList<>();
        /**
         * The properties which will get passed on to the exporters.
         */
        private Properties config;
        /**
         * The size of the buffers used to write to the files.
         */
        private static final int BUFFER_SIZE = 1024;
        /**
         * The temporary directory to use.
         */
        private String tempDir;
        /**
         * The default temporary directory to use.
         */
        private static final String DEFAULT_TEMP_DIR = System.getProperty("java.io.tmpdir") + "ODVExporter";
        /**
         * The compression level used with the ZIP-archive.
         */
        private static final int COMPRESSION_LEVEL = 9;
        /**
         * Progress value indicating loading of configuration.
         */
        private static final int PROGRESS_LOADED_CONFIG = 33;
        /**
         * Progress value indicating all exporters were tried.
         */
        private static final int PROGRESS_ALL_EXPORTERS_DONE = 66;
        /**
         * Progress value indicating all generated files zipped up.
         */
        private static final int PROGRESS_ZIPPED_FILES = 100;

        /**
         * Constructor to set the default values.
         */
        public ODVExporterImplementation() {
            this.tempDir = DEFAULT_TEMP_DIR;
        }

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
         * Unused, thus unimplemented.
         *
         * @param entries Nothing here.
         * @throws IllegalArgumentException No thrown as this will not change the state of the exporter.
         */
        @Override
        public void setEntries(final List<?> entries) throws IllegalArgumentException {
            LOG.log(Level.WARNING, "Tried to set entries but this it not possible with this exporter");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int exportDataToFile(final List<VaultEntry> data) {
            FileOutputStream fileOutputStream;
            ZipOutputStream zipOutputStream;
            Map<String, MetaValues> metaData = new HashMap<>();
            try {
                fileOutputStream = new FileOutputStream(exportFilePath);
            } catch (FileNotFoundException exception) {
                LOG.log(Level.SEVERE, "Could not open output stream " + exportFilePath);
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            zipOutputStream = new ZipOutputStream(fileOutputStream, Charset.forName("UTF-8"));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            zipOutputStream.setLevel(COMPRESSION_LEVEL);
            File file = new File(tempDir);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    LOG.log(Level.SEVERE, "Could not create temporary folder");
                    return ReturnCode.RESULT_ERROR.getCode();
                }
            }
            PluginManager manager = new DefaultPluginManager();
            manager.loadPlugins();
            manager.startPlugins();
            List<Exporter> exporters = manager.getExtensions(Exporter.class);
            for (Exporter exporter : exporters) {
                String name = exporter.getClass().getName().replaceAll(".*\\$", "")
                        .replace("Implementation", "");
                if (name.contains("ODVExporter") || metaData.containsKey(name)) {
                    continue;
                }
                MetaValues thisEntryMetaData = new MetaValues();
                String exportFile = tempDir + File.separator + name + ".export";
                exporter.setExportFilePath(exportFile);
                exporter.loadConfiguration(config);
                exporter.registerStatusCallback(new StatusListener() {
                    @Override
                    public void onStatusCallback(final int progress, final String status) {
                        notifyStatus(progress, name + ": " + status);
                    }
                });
                try {
                    exporter.exportDataToFile(data);
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Could not export with exporter: " + name);
                    continue;
                }
                String checksum;
                try {
                    checksum = makeChecksum(exportFile);
                } catch (Exception exception) {
                    return ReturnCode.RESULT_ERROR.getCode();
                }
                thisEntryMetaData.file = exportFile;
                thisEntryMetaData.checksum = checksum;
                metaData.put(name, thisEntryMetaData);
            }
            String metaFile;
            try {
                metaFile = makeMetaFile(metaData);
            } catch (IOException exception) {
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            notifyStatus(PROGRESS_ALL_EXPORTERS_DONE, "Done exporting with all available exporters");
            // Adding all generated export files and the meta file to the zip
            try {
                Iterator iterator = metaData.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry metaEntry = (Map.Entry) iterator.next();
                    MetaValues metaValues = (MetaValues) metaEntry.getValue();
                    addFileToZip(metaValues.file, zipOutputStream);
                }
                addFileToZip(metaFile, zipOutputStream);
            } catch (Exception exception) {
                    return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }
            try {
                zipOutputStream.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Couldn't close zipOutputStream " + ex);
            }
            notifyStatus(PROGRESS_ZIPPED_FILES, "All generated export files zipped");
            return ReturnCode.RESULT_OK.getCode();
        }

        /**
         * This method generate the checksum in form of an SHA-512 hash for the created ZIP-archive.
         *
         * @param fileName The name of the ZIP to generate the checksum for
         * @return The SHA-512 checksum of the file.
         * @throws IOException Thrown if the streams could not be opened for reading/writing the files.
         * @throws NoSuchAlgorithmException Thrown if the SHA-512 hash is not available on the system.
         */
        private String makeChecksum(final String fileName) throws IOException, NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] dataBytes = new byte[BUFFER_SIZE];
            int nextRead;
            try {
                while ((nextRead = inputStream.read(dataBytes)) != -1) {
                    digest.update(dataBytes, 0, nextRead);
                }
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, "Could not generate checksum for file " + fileName);
                throw exception;
            } finally {
                inputStream.close();
            }
            return (new HexBinaryAdapter()).marshal(digest.digest());
        }

        /**
         * This adds the named file to the given {@link ZipOutputStream} as a {@link ZipEntry}.
         *
         * @param file The name of the file to be added.
         * @param zipStream The zip to add the file to.
         * @throws IOException Thrown if the file could not be added to the zip.
         */
        private void addFileToZip(final String file, final ZipOutputStream zipStream) throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            ZipEntry zipEntry = new ZipEntry(file.replace(tempDir + File.separator, ""));
            zipStream.putNextEntry(zipEntry);
            File zipFile = new File(file);
            FileInputStream in = new FileInputStream(zipFile);
            try {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, len);
                }
            } catch (IOException exception) {
                LOG.log(Level.SEVERE, "Could not add file " + file + " to ZIP-archive");
                throw exception;
            } finally {
                in.close();
                zipStream.closeEntry();
            }
        }

        /**
         * This method generates the meta file for the ZIP-archive.
         * It contains which files where generated by exporters.
         *
         * @param metaData The list of exporters that were used as well as the files they generated and their checksums.
         * @return The name of the generated meta file.
         * @throws IOException Thrown if the file could not be created.
         */
        private String makeMetaFile(final Map<String, MetaValues> metaData) throws IOException {
            final String metaFile = tempDir + File.separator + "meta.info";
            FileOutputStream outputStream = new FileOutputStream(metaFile);
            Iterator iterator = metaData.entrySet().iterator();
            try {
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    String exporter = pair.getKey().toString();
                    MetaValues data = (MetaValues) pair.getValue();
                    String file = data.file.replace(tempDir + File.separator, "");
                    String checksum = data.checksum;
                    String line = exporter + "=" + file + "=" + checksum + "\n";
                    outputStream.write(line.getBytes(Charset.forName("UTF-8")));
                }
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, "Couldn't generate meta file for ZIP-archive");
                throw exception;
            } finally {
                outputStream.close();
            }
            return  metaFile;
        }

        /**
         * This loads the config for this plugin and also stores it to be passed on to the individual importers.
         *
         * @param configuration The configuration object.
         * @return True if the configuration has been stored.
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            if (configuration == null) {
                LOG.log(Level.WARNING, "No configuration given, assuming default values and no period restriction");
                config = new Properties();
            } else {
                config = configuration;
                if (configuration.containsKey("temporaryDirectory")) {
                    tempDir = configuration.getProperty("temporaryDirectory");
                }
            }
            if (configuration.containsKey("compatiblePlugins")) {
                this.compatiblePlugins.addAll(Arrays.asList(configuration.getProperty("compatiblePlugins").split("\\s*,\\s*")));
            }
            notifyStatus(PROGRESS_LOADED_CONFIG, "Loaded configuration");
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getHelpFilePath() {
            //still missing
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getListOfCompatiblePluginIDs() {
            return this.compatiblePlugins;
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

        /**
         * This class encapsulates the values used in the meta data.
         * Namely the file that the exporters generated as well as its checksum.
         */
        private static class MetaValues {
            /**
             * This name of the file generated an exporter.
             */
            private String file;
            /**
             * The checksum for the file generated by an exporter.
             */
            private String checksum;
        }
    }
}
