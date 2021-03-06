package de.opendiabetes.tests.plugin.exporter;

import de.opendiabetes.vault.container.SliceEntry;
import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SliceLayoutCSVExporterTest {

    @Test
    public void pluginStart()  {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        manager.getPluginFromString(OpenDiabetesPlugin.class, "SliceLayoutCSVExporter");

    }


    /**
     * Test to see whether there is a SEVERE Warning when passing wrong input to setAdditional
     */
    /*@Test
    public void setAdditionalException() {
        Exporter sliceLayoutCSVExporter = OpenDiabetesPluginManager.getInstance().getPluginFromString(Exporter.class, "SliceLayoutCSVExporter");
        Handler handler = new Handler() {
            String logOut = "";
            int msgsReceived = 0;

            @Override
            public void publish(final LogRecord record) {
                logOut += record.getLevel().getName() + ": " + record.getMessage();
                msgsReceived++;
                Assert.assertTrue(logOut.contains("SEVERE: No data supplied to be set"));
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
                Assert.assertTrue(msgsReceived > 0);

            }
        };
        sliceLayoutCSVExporter.LOG.addHandler(handler);

        sliceLayoutCSVExporter.setEntries(new ArrayList<SliceEntry>());

        sliceLayoutCSVExporter.LOG.getHandlers()[0].close();

    }*/

    /**
     * Test to see whether load configuration returns the correct log.
     */
    @Test
    public void printLogOnLoadConfiguration() {
        Exporter SliceLayoutCSVExporter = OpenDiabetesPluginManager.getInstance().getPluginFromString(Exporter.class, "SliceLayoutCSVExporter");

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
        SliceLayoutCSVExporter.LOG.addHandler(handler);

        //load properties from file
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("properties/SliceLayoutCSVExporter.properties");
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


        //check with wrong dates
        config.remove("periodRestrictionTo");
        SliceLayoutCSVExporter.loadConfiguration(config);

        config.remove("periodRestrictionFrom");
        SliceLayoutCSVExporter.loadConfiguration(config);

        config.setProperty("periodRestrictionFrom", "12/12/2017");
        config.setProperty("periodRestrictionTo", "12/11/2017");
        SliceLayoutCSVExporter.loadConfiguration(config);

        //check no restriction => all get exported
        config = new Properties();
        config.setProperty("periodRestriction", "false");
        SliceLayoutCSVExporter.loadConfiguration(config);

        // check wrong format of date
        config.setProperty("periodRestriction", "True");
        config.setProperty("periodRestrictionFrom", "12.03.98");
        config.setProperty("periodRestrictionTo", "14.03.99");
        SliceLayoutCSVExporter.loadConfiguration(config);

        SliceLayoutCSVExporter.LOG.getHandlers()[0].close();
    }
}
