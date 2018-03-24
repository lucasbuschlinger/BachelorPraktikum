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
package de.opendiabetes.tests.plugin.management;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.data.VaultDAO;
import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImportAndInterpretDataflowHelper {

    /**
     *
     * @param importer
     * @param interpreters
     * @return
     */
    public List<VaultEntry> importAndInterpretWithoutDb(final FileImporter importer, final String path, final List<Interpreter> interpreters) throws Exception {
        // parse file
        List<VaultEntry> result = importer.importData(path);
        result = interpret(result, interpreters);
        return result;
    }


    /**
     * //TODO javadoc
     */
    public void importAndInterpret(final FileImporter importer, final String path, final List<Interpreter> interpreters, final VaultDAO db) throws Exception {
        // parse file
        List<VaultEntry> result = importer.importData(path);
        result = interpret(result, interpreters);
        addEntriesToDB(db, result);
    }

    /**
     * /TODO javadoc
     * @param input
     * @param interpreters
     * @return
     */
    private List<VaultEntry> interpret(final List<VaultEntry> input, final List<Interpreter> interpreters){
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
     *
     * @param db
     * @param entries
     */
    private void addEntriesToDB(final VaultDAO db, final List<VaultEntry> entries){
        // update DB
        for (VaultEntry item : entries) {
            // put in db
            db.putEntry(item);
        }
        db.removeDuplicates();
    }
}
