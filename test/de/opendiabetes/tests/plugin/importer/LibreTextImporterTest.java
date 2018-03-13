package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.PluginException;

import java.io.FileNotFoundException;

public class LibreTextImporterTest {

    /**
     * Test to see whether the plugin can be started.
     *
     * @throws PluginException If the plugin can not be started.
     */
    @Test
    public void pluginStart() throws PluginException {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        manager.getPluginFromString(FileImporter.class, "LibreTextImporter");
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        FileImporter libreTextImporter = manager.getPluginFromString(FileImporter.class, "LibreTextImporter");

        try {
            libreTextImporter.importData("path/to/data");
        } catch (FileNotFoundException exception) {
            Assert.assertNotNull(exception);
        } catch (Exception exception) {
            Assert.fail("Should have thrown FileNotFoundException");
        }
    }
}
