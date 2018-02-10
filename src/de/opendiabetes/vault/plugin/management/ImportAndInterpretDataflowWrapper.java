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

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImportAndInterpretDataflowWrapper {

    /**
     *
     * @param importer
     * @param interpreters
     * @return
     */
    public List<VaultEntry> importAndInterpretWithoutDb(final Importer importer, final List<Interpreter> interpreters) {
        // parse file
        if (!importer.importData()) {
            return null;
        }

        List<VaultEntry> result = importer.getImportedData();
        result = interpret(result, interpreters);
        return result;
    }


    /**
     * //TODO javadoc
     */
    public void importAndInterpret(final Importer importer, final List<Interpreter> interpreters, final VaultDao db) {
        // parse file
        if (!importer.importData()) {
            return;
        }

        List<VaultEntry> result = importer.getImportedData();
        result = interpret(result, interpreters);
        addEntriesToDB(db, result, importer.getImportedRawData());
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
     * @param rawEntries
     */
    private void addEntriesToDB(final VaultDao db, final List<VaultEntry> entries, final  List<RawEntry> rawEntries){
        for (RawEntry item : rawEntries) { // not null since importFile is called
            item.setId(db.putRawEntry(item));
        }

        // update DB
        for (VaultEntry item : entries) {
            // update raw id (if there is a corresponding raw entry)
            if (item.getRawId() > 0) {
                RawEntry rawEntry = rawEntries.get((int) item.getRawId());
                item.setRawId(rawEntry.getId());
            }
            // put in db
            db.putEntry(item);
        }

        db.removeDuplicates();
    }
}
