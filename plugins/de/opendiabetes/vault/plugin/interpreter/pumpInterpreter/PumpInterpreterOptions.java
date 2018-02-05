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
package de.opendiabetes.vault.plugin.interpreter.pumpInterpreter;

import de.opendiabetes.vault.plugin.interpreter.InterpreterOptions;

import java.util.Date;

/**
 * @author Jens
 */
public class PumpInterpreterOptions extends InterpreterOptions {
    /**
     * //TODO javadoc
     */
    public final boolean fillCanulaAsNewKatheder;

    /**
     * //TODO javadoc
     */
    public final int fillCanulaCooldown;


    /**
     * //TODO javadoc
     *
     * @param fillCanulaAsNewKatheder
     * @param fillCanulaCooldown
     * @param isImportPeriodRestricted
     * @param importPeriodFrom
     * @param importPeriodTo
     */
    public PumpInterpreterOptions(final boolean fillCanulaAsNewKatheder,
                                  final int fillCanulaCooldown, final boolean isImportPeriodRestricted,
                                  final Date importPeriodFrom, final Date importPeriodTo) {
        super(isImportPeriodRestricted, importPeriodFrom, importPeriodTo);
        this.fillCanulaAsNewKatheder = fillCanulaAsNewKatheder;
        this.fillCanulaCooldown = fillCanulaCooldown;
    }


}
