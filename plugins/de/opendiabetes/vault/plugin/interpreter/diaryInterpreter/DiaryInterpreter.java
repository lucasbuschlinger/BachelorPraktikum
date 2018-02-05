/*
 * Copyright (C) 2017 Jens Heuschkel
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
package de.opendiabetes.vault.plugin.interpreter.diaryInterpreter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.interpreter.InterpreterOptions;
import de.opendiabetes.vault.plugin.interpreter.vaultInterpreter.VaultInterpreter;

import java.util.List;

/**
 * @author juehv
 */
public class DiaryInterpreter extends VaultInterpreter {

    /**
     * Constructor.
     *
     * @param importer the Importer to use??//TODO
     * @param options  interpreter options
     * @param db       the database to import to
     */
    public DiaryInterpreter(final Importer importer,
                            final InterpreterOptions options, final VaultDao db) {
        super(importer, options, db);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected List<VaultEntry> interpret(final List<VaultEntry> result) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
