/**
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tests for the MedtronicImporter plugin.
 */
public class MedtronicImporterTest {

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
        manager.enablePlugin("MedtronicImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("MedtronicImporter"));
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("MedtronicImporter");
        manager.startPlugin("MedtronicImporter");
        Importer medtronicImporter = manager.getExtensions(Importer.class).get(0);
        medtronicImporter.setImportFilePath("path/to/data");
        Assert.assertFalse(medtronicImporter.importData());
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Importer medtronicImporter = TestImporterUtil.getImporter("MedtronicImporter");
        medtronicImporter.setImportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", medtronicImporter.getImportFilePath());
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Importer medtronicImporter = TestImporterUtil.getImporter("MedtronicImporter");

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/medtronic.properties");
            config.load(input);

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("path to configuration not found, aborting test");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        Assert.assertTrue(medtronicImporter.loadConfiguration(config));


        //check validity check
        config.setProperty("delimiter", "\t");
        Assert.assertTrue(medtronicImporter.loadConfiguration(config));

        config.setProperty("delimiter", ";");
        Assert.assertTrue(medtronicImporter.loadConfiguration(config));

        config.setProperty("delimiter", ",");
        Assert.assertTrue(medtronicImporter.loadConfiguration(config));

        config.setProperty("delimiter", "not a valid delimiter");
        Assert.assertFalse(medtronicImporter.loadConfiguration(config));

        //not providing a value for delimiter
        config = new Properties();
        config.setProperty("another property", "value");
        Assert.assertFalse(medtronicImporter.loadConfiguration(config));


        config.setProperty("delimiter", "");
        Assert.assertFalse(medtronicImporter.loadConfiguration(config));

    }

    /*
    @Test
    public void smartDelimiterDetectionTest() {
        Importer medtronicImporter = TestImporterUtil.getImporter("MedtronicImporter");
        String dataPath = "/home/magnus/Downloads/CareLink-Export-1486459734778.csv";
        medtronicImporter.setImportFilePath(dataPath);
        medtronicImporter.importData();
        return;
    }*/

    //TODO add test for notifyMechanism

}
