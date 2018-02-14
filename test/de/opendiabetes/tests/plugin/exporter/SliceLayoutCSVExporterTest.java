package de.opendiabetes.tests.plugin.exporter;

import de.opendiabetes.tests.plugin.importer.TestImporterUtil;
import de.opendiabetes.vault.container.SliceEntry;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class SliceLayoutCSVExporterTest {

    @Test
    public void pluginStart()  {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("SliceLayoutCSVExporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("SliceLayoutCSVExporter"));
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Exporter SliceLayoutCSVExporter = TestImporterUtil.getExporter("SliceLayoutCSVExporter");
        SliceLayoutCSVExporter.setExportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", SliceLayoutCSVExporter.getExportFilePath());
    }

    /**
     * Test to see whether the needed database can be set.
     */
    @Test
    public void setEntries() {
        Exporter sliceLayoutCSVExporter = TestImporterUtil.getExporter("SliceLayoutCSVExporter");
        sliceLayoutCSVExporter.setEntries(new ArrayList<SliceEntry>());
    }

    /**
     * Test to see whether the IllegalArgumentException gets thrown when passing wrong input to setAdditional
     */
    @Test(expected =  IllegalArgumentException.class)
    public void setAdditionalException() {
        Exporter sliceLayoutCSVExporter = TestImporterUtil.getExporter("SliceLayoutCSVExporter");
        sliceLayoutCSVExporter.setEntries(new ArrayList<SliceEntry>());
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Exporter SliceLayoutCSVExporter = TestImporterUtil.getExporter("SliceLayoutCSVExporter");

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/slicelayoutcsvexporter.properties");
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

        Assert.assertTrue(SliceLayoutCSVExporter.loadConfiguration(config));


        //check which wrong dates
        config.remove("periodRestrictionTo");
        Assert.assertFalse(SliceLayoutCSVExporter.loadConfiguration(config));

        config.remove("periodRestrictionFrom");
        Assert.assertFalse(SliceLayoutCSVExporter.loadConfiguration(config));

        config.setProperty("periodRestrictionFrom", "12/12/2017");
        config.setProperty("periodRestrictionTo", "12/11/2017");
        Assert.assertFalse(SliceLayoutCSVExporter.loadConfiguration(config));

        //check no restriction => all get exported
        config = new Properties();
        config.setProperty("periodRestriction", "false");
        Assert.assertTrue(SliceLayoutCSVExporter.loadConfiguration(config));

        // check wrong format of date
        config.setProperty("periodRestriction", "True");
        config.setProperty("periodRestrictionFrom", "12.03.98");
        config.setProperty("periodRestrictionTo", "14.03.99");
        Assert.assertFalse(SliceLayoutCSVExporter.loadConfiguration(config));
    }
}
