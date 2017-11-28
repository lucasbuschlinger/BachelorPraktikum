package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.importer.Importer;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import java.nio.file.Paths;

public class MedtronicImporterTest {

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
     *
     * @throws PluginException If the plugin can not be started
     */
    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("MedtronicImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("MedtronicImporter"));

    }

    /**
     * Test to see whether the plugin can be called and accessed
     */
    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));//{
        //    @Override
        //    public RuntimeMode getRuntimeMode(){return RuntimeMode.byName("development");}
        //};
        manager.loadPlugins();
        manager.enablePlugin("MedtronicImporter");
        manager.startPlugin("MedtronicImporter");
        Importer medtronicImporter = manager.getExtensions(Importer.class).get(0);
        medtronicImporter.setImportFilePath("path/to/data");
        Assert.assertFalse(medtronicImporter.importData());
    }

    /**
     * Test for the path setter and getter
     */
    @Test
    public void setGetPath() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin("MedtronicImporter");
        Importer medtronicImporter = manager.getExtensions(Importer.class).get(0);
        medtronicImporter.setImportFilePath("path/to/import/file");
        Assert.assertEquals("path/to/import/file", medtronicImporter.getImportFilePath());
    }

    /**
     * Test for invalid path
     *
     @Test public void invalidPath(){
     PluginManager manager = new DefaultPluginManager(Paths.get("export"));
     manager.loadPlugins();
     manager.startPlugin("MedtronicImporter");
     Importer medtronicImporter = manager.getExtensions(Importer.class).get(0);
     medtronicImporter.setImportFilePath("not/a/valid/path");
     Assert.assertFalse(medtronicImporter.importData());
     }*/


}
