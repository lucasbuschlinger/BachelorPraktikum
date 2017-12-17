package de.opendiabetes.tests.plugin.exporter;

import de.opendiabetes.tests.plugin.importer.TestImporterUtil;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

public class VaultCSVExporterTest {

    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("VaultCSVExporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("VaultCSVExporter"));
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Exporter vaultCSVExporter = TestImporterUtil.getExporter("VaultCSVExporter");
        vaultCSVExporter.setExportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", vaultCSVExporter.getExportFilePath());
    }

    /**
     * Test to see whether the needed database can be set.
     */
    @Test
    public void setDatabase() {
        Exporter vaultODVExporter = TestImporterUtil.getExporter("VaultODVExporter");
        try {
            VaultDao.initializeDb();
        } catch (SQLException exception){
            Assert.fail("Initialization of the VaultDao database went wrong.");
        }
        vaultODVExporter.setDatabase(VaultDao.getInstance());
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Exporter vaultCSVExporter = TestImporterUtil.getExporter("VaultCSVExporter");

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/vaultcsvexporter.properties");
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

        Assert.assertTrue(vaultCSVExporter.loadConfiguration(config));


        //check which wrong dates
        config.remove("periodRestrictionTo");
        Assert.assertFalse(vaultCSVExporter.loadConfiguration(config));

        config.remove("periodRestrictionFrom");
        Assert.assertFalse(vaultCSVExporter.loadConfiguration(config));

        config.setProperty("periodRestrictionFrom", "12/12/2017");
        config.setProperty("periodRestrictionTo", "12/11/2017");
        Assert.assertFalse(vaultCSVExporter.loadConfiguration(config));

        //check no restriction => all get exported
        config = new Properties();
        config.setProperty("periodRestriction", "false");
        Assert.assertTrue(vaultCSVExporter.loadConfiguration(config));

        // check wrong format of date
        config.setProperty("periodRestriction", "True");
        config.setProperty("periodRestrictionFrom", "12.03.98");
        config.setProperty("periodRestrictionTo", "14.03.99");
        Assert.assertFalse(vaultCSVExporter.loadConfiguration(config));
    }
}
