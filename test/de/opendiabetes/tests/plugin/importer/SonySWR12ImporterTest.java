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
package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.fileimporter.FileImporter;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * Tests for the SonySWR21Importer plugin.
 */
public class SonySWR12ImporterTest {

    /**
     * Test to see whether the plugin can be loaded.
     */
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        Assert.assertTrue(0 != manager.getPlugins().size());
    }

    /**
     * Test to see whether the plugin can be started.
     *
     * @throws PluginException If the plugin can not be started.
     */
    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("SonySWR12Importer");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("SonySWR12Importer"));
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        FileImporter sonySWR12Importer = (FileImporter) TestImporterUtil.getImporter("SonySWR12Importer");
        try {
            sonySWR12Importer.importData("path/to/data");
        } catch (FileNotFoundException exception) {
            Assert.assertNotNull(exception);
        } catch (Exception exception) {
            Assert.fail("Should have thrown FileNotFoundException");
        }
    }
}
