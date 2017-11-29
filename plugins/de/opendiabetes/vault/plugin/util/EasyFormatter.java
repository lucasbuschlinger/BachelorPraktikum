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

import java.util.Locale;

/**
 * This class implements an easy formatter for TODO.
 */
public class EasyFormatter {

    /**
     * Format of the output.
     */
    public static final String DOUBLE_FORMAT = "%1$.2f";

    /**
     * TODO.
     * @param input The double to be formatted.
     * @return The formatted double as a string.
     */
    public static String formatDouble(double input) {
        return String.format(Locale.ENGLISH, DOUBLE_FORMAT, input).replace(",", "");
    }
}
