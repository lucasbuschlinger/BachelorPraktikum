/*
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.common.AbstractPlugin;

import java.util.List;

/**
 * Most abstract importer, implements Importer interface.
 * All actual importer plugins are descendants of this class.
 * Supplies a Logger {@link Importer#LOG }.
 * Handles status listeners {@link AbstractImporter#listeners}.
 *
 * @author Magnus Gärtner
 * @author Lucas Buschlinger
 */
public abstract class AbstractImporter extends AbstractPlugin implements Importer {

}
