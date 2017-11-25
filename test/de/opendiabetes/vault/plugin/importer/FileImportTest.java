package de.opendiabetes.vault.plugin.importer;


import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;
import java.nio.file.Paths;

public class FileImportTest {
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("manualPlugins"));
        manager.loadPlugins();
        Assert.assertEquals(1, manager.getPlugins().size());
    }

    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("manualPlugins"));
        manager.loadPlugins();
        manager.enablePlugin("FileImporter");
        manager.startPlugins();
        System.out.println(manager.getPlugins().get(0).getPlugin());
        Assert.assertTrue(manager.enablePlugin("FileImporter"));

    }

    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        System.out.println(manager.getExtensions(Importer.class));
        //System.out.println(manager.getExtensions(FileImporter.class));

    }

}
