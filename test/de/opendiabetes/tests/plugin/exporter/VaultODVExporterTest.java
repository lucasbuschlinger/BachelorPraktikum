package de.opendiabetes.tests.plugin.exporter;

import de.opendiabetes.tests.plugin.importer.TestImporterUtil;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class VaultODVExporterTest {

    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("VaultODVExporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("VaultODVExporter"));
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Exporter vaultODVExporter = TestImporterUtil.getExporter("VaultODVExporter");
        vaultODVExporter.setExportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", vaultODVExporter.getExportFilePath());
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Exporter vaultODVExporter = TestImporterUtil.getExporter("VaultODVExporter");

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/VaultODVexporter.properties");
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

        Assert.assertTrue(vaultODVExporter.loadConfiguration(config));


        //check which wrong dates
        config.remove("periodRestrictionTo");
        Assert.assertFalse(vaultODVExporter.loadConfiguration(config));

        config.remove("periodRestrictionFrom");
        Assert.assertFalse(vaultODVExporter.loadConfiguration(config));

        config.setProperty("periodRestrictionFrom", "12/12/2017");
        config.setProperty("periodRestrictionTo", "12/11/2017");
        Assert.assertFalse(vaultODVExporter.loadConfiguration(config));

        //check no restriction => all get exported
        config = new Properties();
        config.setProperty("periodRestriction", "false");
        Assert.assertTrue(vaultODVExporter.loadConfiguration(config));

        // check wrong format of date
        config.setProperty("periodRestriction", "True");
        config.setProperty("periodRestrictionFrom", "12.03.98");
        config.setProperty("periodRestrictionTo", "14.03.99");
        Assert.assertFalse(vaultODVExporter.loadConfiguration(config));
    }
}
