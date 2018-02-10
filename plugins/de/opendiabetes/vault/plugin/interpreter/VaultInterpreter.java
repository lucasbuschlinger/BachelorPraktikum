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
package de.opendiabetes.vault.plugin.interpreter;

import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.common.AbstractPlugin;


/**
 * @author OpenDiabetes
 */
public abstract class VaultInterpreter extends AbstractPlugin implements Interpreter {

    /**
     * //TODO javadoc
     */
    private VaultDao db;

    /**
     * @param importer
     * @param options
     * @param db
     */
    @Override
    public void init(final VaultDao db) {
        this.db = db;
    }

    /**
     * @return
     */
    protected VaultDao getDb() {
        return db;
    }
}
