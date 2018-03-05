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

import de.opendiabetes.vault.plugin.importer.Importer;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Tests for the SonySWR21Importer plugin.
 */
public class ODVDBJsonImporterTest {

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
        manager.enablePlugin("ODVDBJsonImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("ODVDBJsonImporter"));
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        Importer odvImporter = TestImporterUtil.getImporter("ODVDBJsonImporter");
        System.out.println("TEST" + odvImporter.getClass());
        odvImporter.setImportFilePath("path/to/data");
        Assert.assertFalse(odvImporter.importData());
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Importer odvImporter = TestImporterUtil.getImporter("ODVDBJsonImporter");
        odvImporter.setImportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", odvImporter.getImportFilePath());
    }

}
