package de.opendiabetes.tests.plugin.exporter;

import de.opendiabetes.tests.plugin.importer.TestImporterUtil;
import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class VaultCSVExporterTest {

    @Test
    public void pluginStart() throws PluginException {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        manager.getPluginFromString(OpenDiabetesPlugin.class, "VaultCSVExporter");
    }

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Exporter vaultCSVExporter = TestImporterUtil.getExporter("VaultCSVExporter");
        Handler handler = new Handler() {
            String logOut = "";
            @Override
            public void publish(final LogRecord record) {
                logOut += record.getLevel().getName() + ": " + record.getMessage();
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
                Assert.assertTrue(
                        logOut.contains(
                                "WARNING: The exporter's configuration does not specify " +
                                        "whether the data is period restricted, defaulting to no period restriction")
                                && logOut.contains("INFO: Export data is not period restricted by the exporter's configuration.")
                                && logOut.contains("SEVERE: Either of the dates specified in the exporter's configuration is malformed. The expected format is dd/mm/yyyy."));
            }
        };
        vaultCSVExporter.LOG.addHandler(handler);

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/VaultCSVExporter.properties");
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
        vaultCSVExporter.loadConfiguration(config);

        config.remove("periodRestrictionFrom");
        vaultCSVExporter.loadConfiguration(config);

        config.setProperty("periodRestrictionFrom", "12/12/2017");
        config.setProperty("periodRestrictionTo", "12/11/2017");
        vaultCSVExporter.loadConfiguration(config);

        //check no restriction => all get exported
        config = new Properties();
        config.setProperty("periodRestriction", "false");
        vaultCSVExporter.loadConfiguration(config);

        // check wrong format of date
        config.setProperty("periodRestriction", "True");
        config.setProperty("periodRestrictionFrom", "12.03.98");
        config.setProperty("periodRestrictionTo", "14.03.99");
        vaultCSVExporter.loadConfiguration(config);

        vaultCSVExporter.LOG.getHandlers()[0].close();
    }
}
