/**
 * Copyright (C) 2017 OpenDiabetes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.util;


import java.io.*;

/**
 * This class implements a utility to copy files.
 */
public class FileCopyUtil {

    /**
     * Method to copy a file or directory.
     * @param sourceLocation The source file or directory to copy.
     * @param targetLocation The target file or directory to be copied to.
     * @throws IOException Thrown if an invalid file location is given or no matching file types given.
     */
    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    /**
     * Method to copy a directory.
     * @param source The source directory to copy.
     * @param target The target directory to be copied to.
     * @throws IOException Thrown if an invalid directory location is given.
     */
    public static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    /**
     * Method to copy a file.
     * @param source The source file to copy.
     * @param target The target file to be copied to.
     * @throws IOException Thrown if an invalid file location is given.
     */
    public static void copyFile(File source, File target) throws IOException {
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }
}
