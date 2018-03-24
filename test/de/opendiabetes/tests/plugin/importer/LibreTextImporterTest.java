package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.nio.file.Paths;

public class LibreTextImporterTest {

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
        manager.enablePlugin("LibreTextImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("LibreTextImporter"));
    }

    /**
     * Test to see whether the plugin can be called and accessed.
     */
    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("LibreTextImporter");
        manager.startPlugin("LibreTextImporter");
        FileImporter LibreTextImporter = (FileImporter)manager.getExtensions(Importer.class).get(0);
        try {
            LibreTextImporter.importData("path/to/data");
        } catch (IllegalArgumentException exception) {
            Assert.assertNotNull(exception);
        } catch (Exception exception) {
            Assert.fail("Should have thrown FileNotFoundException");
        }
    }
}
