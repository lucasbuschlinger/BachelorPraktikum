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
import de.opendiabetes.vault.plugin.interpreter.VaultInterpreter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.List;
import java.util.Properties;

/**
 * Wrapper class for the DiaryInterpreter plugin.
 *
 * @author Magnus Gärtner
 */
public class DiaryInterpreter extends Plugin {

    /**
     * Constructor for the PluginManager.
     *
     * @param wrapper The PluginWrapper.
     */
    public DiaryInterpreter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the DiaryInterpreter plugin.
     */
    @Extension
    public static class DiaryInterpreterImplementation extends VaultInterpreter {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<VaultEntry> interpret(final List<VaultEntry> input) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * @param configuration
         * @return
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
