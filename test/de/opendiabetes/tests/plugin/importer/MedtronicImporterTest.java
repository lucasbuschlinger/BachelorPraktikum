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

import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.PluginException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Tests for the MedtronicImporter plugin.
 */
public class MedtronicImporterTest {

    /**
     * Test to see whether the plugin can be started.
     *
     * @throws PluginException If the plugin can not be started.
     */
    @Test
    public void pluginStart() throws PluginException {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        manager.getPluginFromString(FileImporter.class, "MedtronicImporter");
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        FileImporter medtronicImporter = manager.getPluginFromString(FileImporter.class, "MedtronicImporter");

        try {
            medtronicImporter.importData("path/to/data");
        } catch (FileNotFoundException exception) {
            Assert.assertNotNull(exception);
        } catch (Exception exception) {
            Assert.fail("Should have thrown FileNotFoundException");
        }
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfigurationCSVImporters() {
        Importer medtronicImporter = OpenDiabetesPluginManager.getInstance().getPluginFromString(Importer.class,"MedtronicImporter");

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/MedtronicImporter.properties");
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


        config.setProperty("delimiter", "");
        Assert.assertFalse(medtronicImporter.loadConfiguration(config));

    }


    /* To test the delimiter detection you need testdata
     * that can not be checked in to github.
     * You have to delete the lib folder from
     * the plugin in order to get this test working.
     */
/*
    @Test
    public void smartDelimiterDetectionTest() throws Exception {
        FileImporter medtronicImporter = OpenDiabetesPluginManager.getInstance().getPluginFromString(FileImporter.class, "MedtronicImporter");
        String dataPath = "/home/magnus/testdata/CareLink-Export-1486459734778.csv";

        medtronicImporter.importData(dataPath);
        return;
    }*/

}
