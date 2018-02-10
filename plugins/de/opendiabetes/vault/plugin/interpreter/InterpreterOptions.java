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

import java.util.Date;

/**
 * @author OpenDiabetes
 */
public class InterpreterOptions {

    /**
     * //TODO javadoc
     */
    public final boolean isImportPeriodRestricted;

    /**
     * //TODO javadoc
     */
    public final Date importPeriodFrom;

    /**
     * //TODO javadoc
     */
    public final Date importPeriodTo;

    /**
     * //TODO javadoc
     *
     * @param isImportPeriodRestricted
     * @param importPeriodFrom
     * @param importPeriodTo
     */
    public InterpreterOptions(final boolean isImportPeriodRestricted,
                              final Date importPeriodFrom, final Date importPeriodTo) {
        this.isImportPeriodRestricted = isImportPeriodRestricted;
        this.importPeriodFrom = importPeriodFrom != null ? importPeriodFrom : new Date();
        this.importPeriodTo = importPeriodTo != null ? importPeriodTo : new Date();
    }


}
