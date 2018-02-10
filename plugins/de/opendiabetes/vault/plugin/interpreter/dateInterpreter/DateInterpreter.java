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
package de.opendiabetes.vault.plugin.interpreter.dateInterpreter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.interpreter.vaultInterpreter.VaultInterpreter;

import java.util.ArrayList;
import java.util.List;

public class DateInterpreter extends VaultInterpreter {
    /**
     * @param input
     * @return
     */
    @Override
    public List<VaultEntry> interpret(final List<VaultEntry> input) {
        if (getOptions().isImportPeriodRestricted) {
            List<VaultEntry> retVal = new ArrayList<>();
            for (VaultEntry item : input) {
                if (item.getTimestamp().after(getOptions().importPeriodFrom)
                        && item.getTimestamp().before(getOptions().importPeriodTo)) {
                    retVal.add(item);
                }
            }
            return retVal;
        } else {
            return input;
        }

    }
}
