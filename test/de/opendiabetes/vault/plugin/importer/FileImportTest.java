package de.opendiabetes.vault.plugin.importer;


import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Unit test for the fileimporter Plugin
 * @see de.opendiabetes.vault.plugin.importer.fileimporter
 */
public class FileImportTest {

    /**
     * Test to see whether the plugin can be loaded
     */
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        Assert.assertTrue(0 != manager.getPlugins().size());
    }

    /**
     * Test to see whether the plugin can be started
     * @throws PluginException If the plugin can not be started
     */
    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("FileImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("FileImporter"));

    }

    /**
     * Test to see whether the plugin can be called and accessed
     */
    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin("FileImporter2");
        Importer importer = manager.getExtensions(Importer.class).get(0);
        importer.importData("path/to/data");
    }

    /**
     * Test for the path setter and getter
     */
    @Test
    public void setGetPath(){
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin("FileImporter");
        Importer fileImporter = manager.getExtensions(Importer.class).get(0);
        fileImporter.setImportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", fileImporter.getImportFilePath());
    }

    /**
     * Test for invalid path
     */
    @Test
    public void invalidPath(){
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin("FileImporter");
        Importer fileImporter = manager.getExtensions(Importer.class).get(0);
        Assert.assertFalse(fileImporter.importData("no/valid/path/here"));

    }
}
