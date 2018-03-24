/*
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
package de.opendiabetes.vault.plugin.management;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.data.VaultDAO;
import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;

import java.util.List;

/**
 * This class exemplary shows how to import, interpret and store files to a database.
 */
public class ImportAndInterpretDataflowHelper {

    /**
     * Imports and interprets data from a file given the filepath,
     * the importer to use and the interpreters to apply sequentially to the imported data.
     * The output of the last interpreter is then returned.
     * @param importer to use for importing the data from a file
     * @param path to the file
     * @param interpreters sequentially applied to the imported data
     * @return the output of the last interpreter.
     * @throws Exception if something goes wrong
     */
    public List<VaultEntry> importAndInterpretWithoutDb(final FileImporter importer,
                                                        final String path,
                                                        final List<Interpreter> interpreters)
            throws Exception {
        // parse file
        List<VaultEntry> result = importer.importData(path);
        result = interpret(result, interpreters);
        return result;
    }


    /**
     * Does the same as {@link #importAndInterpretWithoutDb(FileImporter, String, List)}
     * but exports the result to the given database.
     * @param importer to use for importing the data from a file
     * @param path to the file
     * @param interpreters sequentially applied to the imported data
     * @param db to export the result to.
     * @throws Exception if something goes wrong
     */
    public void importAndInterpret(final FileImporter importer, final String path, final List<Interpreter> interpreters, final VaultDAO db)
            throws Exception {
        // parse file
        List<VaultEntry> result = importer.importData(path);
        result = interpret(result, interpreters);
        addEntriesToDB(db, result);
    }

    /**
     * Sequentially applies interpreters to some input data.
     * @param input the data to interpret
     * @param interpreters the interpreters that are applied sequentially to the input data
     * @return the output of the last interpreter
     */
    private List<VaultEntry> interpret(final List<VaultEntry> input, final List<Interpreter> interpreters) {
        List<VaultEntry> result = input;
        for (Interpreter interpreter : interpreters) {
            if (result == null || result.isEmpty()) {
                return result;
            }
            result = interpreter.interpret(result);
        }
        return  result;
    }

    /**
     * Adds a list of VaultEntries to the database.
     * @param db the database
     * @param entries to add to the database
     */
    private void addEntriesToDB(final VaultDAO db, final List<VaultEntry> entries) {
        // update DB
        for (VaultEntry item : entries) {
            // put in db
            db.putEntry(item);
        }
        db.removeDuplicates();
    }
}
