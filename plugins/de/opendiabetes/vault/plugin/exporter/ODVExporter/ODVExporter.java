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
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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

            String tempDir = System.getProperty("java.io.tempdir");

            try {
                File file = new File(tempDir + "/ODVExporter");
                file.mkdir();
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, "Could not create temporary folder");
                return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
            }

            tempDir += "/ODVExporter";


            PluginManager manager = new DefaultPluginManager();
            manager.loadPlugins();
            manager.startPlugins();

            List<Exporter> exporters = manager.getExtensions(Exporter.class);

            for (Exporter exporter : exporters) {
                String name = exporter.getClass().getName();
                if (exporter instanceof VaultExporter) {
                    exporter.setAdditional(database);
                }
                String exportFile = tempDir + name;
                exporter.setExportFilePath(exportFile);
                exporter.loadConfiguration(config);
                exporter.registerStatusCallback(new StatusListener() {
                    @Override
                    public void onStatusCallback(final int progress, final String status) {
                        notifyStatus(progress, name + ": " + status);
                    }
                });
                exporter.exportDataToFile(null);
                exportFiles.put(name, exportFile);
            }
            Iterator iterator = exportFiles.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                try {
                    addToZipFile((String) pair.getValue(), zipOutputStream);
                } catch (Exception exception) {
                    LOG.log(Level.SEVERE, "Could not add file to ZIP");
                    return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
                }
            }
            return 0;
        }

        private static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

            System.out.println("Writing '" + fileName + "' to zip file");

            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
            fis.close();
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
