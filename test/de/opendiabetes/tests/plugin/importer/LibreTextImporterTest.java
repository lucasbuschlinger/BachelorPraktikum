package de.opendiabetes.tests.plugin.importer;

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
        Importer LibreTextImporter = manager.getExtensions(Importer.class).get(0);
        LibreTextImporter.setImportFilePath("path/to/data");
        Assert.assertFalse(LibreTextImporter.importData());
    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void setGetPath() {
        Importer libreTextImporter = TestImporterUtil.getImporter("LibreTextImporter");
        libreTextImporter.setImportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", libreTextImporter.getImportFilePath());
        Assert.assertFalse(libreTextImporter.importData());
    }
}
