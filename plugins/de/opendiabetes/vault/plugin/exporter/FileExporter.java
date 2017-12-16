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

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * This class defines the default structure how data gets exported to a file.
 *
 * @author Lucas Buschlinger
 */
public abstract class FileExporter extends AbstractExporter {

    /**
     * The fileOutputStream used to write to the file.
     */
    private FileOutputStream fileOutputStream;
    /**
     * Option whether the data to export is period restricted.
     * By default the export data is not period restricted.
     */
    private boolean isPeriodRestricted = false;
    /**
     * Option which indicates from which point in time the data is period restricted.
     */
    private Date exportPeriodFrom = new Date();
    /**
     * Option which indicates to which point in time the data is period restricted.
     */
    private Date exportPeriodTo = new Date();

    /**
     * {@inheritDoc}
     */
    public int exportDataToFile(final List<VaultEntry> data) {
        // Status update constants.
        final int startWriteProgress = 80;
        final int writeDoneProgress = 100;
        String filePath = this.getExportFilePath();
        // check file stuff
        File checkFile = new File(filePath);
        if (checkFile.exists()
                && (!checkFile.isFile() || !checkFile.canWrite())) {
            return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
        }
        try {
            fileOutputStream = new FileOutputStream(checkFile);
        } catch (FileNotFoundException exception) {
            LOG.log(Level.SEVERE, "Error accessing file for output stream", exception);
            return ReturnCode.RESULT_FILE_ACCESS_ERROR.getCode();
        }

        // create csv data
        List<ExportEntry> exportData = prepareData(data);
        if (exportData == null || exportData.isEmpty()) {
            return ReturnCode.RESULT_NO_DATA.getCode();
        }
        this.notifyStatus(startWriteProgress, "Starting writing to file");
        // write to file
        try {
            writeToFile(exportData);
        } catch (IOException exception) {
            LOG.log(Level.SEVERE, "Error writing odv csv file: {0}" + filePath, exception);
            return ReturnCode.RESULT_ERROR.getCode();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException exception) {
                LOG.log(Level.WARNING, "Error while closing the fileOutputStream, uncritical.", exception);
            }
        }

        this.notifyStatus(writeDoneProgress, "Writing to file successful, all done.");

        return ReturnCode.RESULT_OK.getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeToFile(final List<ExportEntry> data) throws IOException {
        FileChannel channel = fileOutputStream.getChannel();
        byte[] lineFeed = "\n".getBytes(Charset.forName("UTF-8"));

        for (ExportEntry entry : data) {
            byte[] messageBytes = entry.toByteEntryLine();
            channel.write(ByteBuffer.wrap(messageBytes));
            channel.write(ByteBuffer.wrap(lineFeed));
        }

        channel.close();
    }

    /**
     * Getter for the fileOutputStream for descending classes.
     *
     * @return The {@link #fileOutputStream}.
     */
    protected FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    /**
     * Getter for the period restriction flag.
     *
     * @return True if the data is period restricted, false otherwise.
     */
    protected boolean getIsPeriodRestricted() {
        return isPeriodRestricted;
    }

    /**
     * Setter for the period restriction flag {@link #isPeriodRestricted}.
     *
     * @param periodRestricted The value to set the flag to,
     *                         true if the data is period restricted, false otherwise.
     */
    protected void setIsPeriodRestricted(final boolean periodRestricted) {
        this.isPeriodRestricted = periodRestricted;
    }

    /**
     * Setter for the option {@link #exportPeriodFrom}.
     *
     * @param exportPeriodFrom The date to be set.
     */
    protected void setExportPeriodFrom(final Date exportPeriodFrom) {
        this.exportPeriodFrom = exportPeriodFrom;
    }

    /**
     * Getter for the option {@link #exportPeriodFrom}.
     *
     * @return The date the data is period limited from.
     */
    protected Date getExportPeriodFrom() {
        return exportPeriodFrom;
    }

    /**
     * Setter for the option {@link #exportPeriodTo}.
     *
     * @param exportPeriodTo The date to be set.
     */
    protected void setExportPeriodTo(final Date exportPeriodTo) {
        this.exportPeriodTo = exportPeriodTo;
    }

    /**
     * Getter for the option {@link #exportPeriodTo}.
     *
     * @return The date the data is period limited from.
     */
    protected Date getExportPeriodTo() {
        return exportPeriodTo;
    }
}
