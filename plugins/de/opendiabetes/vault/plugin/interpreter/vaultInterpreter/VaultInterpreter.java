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
package de.opendiabetes.vault.plugin.interpreter.vaultInterpreter;

import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.common.AbstractPlugin;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;
import de.opendiabetes.vault.plugin.interpreter.InterpreterOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author OpenDiabetes
 */
public abstract class VaultInterpreter extends AbstractPlugin implements Interpreter {
    /**
     *
     */
    private InterpreterOptions options;
    /**
     * //TODO javadoc
     */
    private VaultDao db;

    /**
     * @return
     */
    protected InterpreterOptions getOptions() {
        return options;
    }

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

    /**
     * @param configuration
     * @return
     */
    @Override
    public boolean loadConfiguration(final Properties configuration) {
        if (!configuration.containsKey("ImportPeriodRestricted")) {
            return false;
        }
        String restriction = configuration.getProperty("ImportPeriodRestricted");
        boolean isImportPeriodRestricted = false;
        switch (restriction) {
            case "True":
            case "1":
            case "y":
            case "yes":
            case "true":
                isImportPeriodRestricted = true;
                break;
            case "False":
            case "0":
            case "n":
            case "no":
            case "false":
                isImportPeriodRestricted = false;
                break;
            default:
                return false;
        }
        if (!isImportPeriodRestricted) {
            options = new InterpreterOptions(isImportPeriodRestricted, null, null);
            return true;
        }

        if (!configuration.containsKey("importPeriodFrom") || !configuration.containsKey("importPeriodTo")) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final Date importPeriodFrom;
        final Date importPeriodTo;
        try {
            importPeriodFrom = dateFormat.parse(configuration.getProperty("importPeriodFrom"));
            importPeriodTo = dateFormat.parse(configuration.getProperty("importPeriodTo"));
        } catch (ParseException e) {
            return false;
        }
        options = new InterpreterOptions(isImportPeriodRestricted, importPeriodFrom, importPeriodTo);
        return true;

    }
}
