package de.opendiabetes.vault.plugin.importer;


import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import java.nio.file.Paths;

public class FileImportTest {
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("ourPlugins"));
        manager.loadPlugins();
        Assert.assertEquals(1, manager.getPlugins().size());
    }

    @Test
    public void pluginStart() {
        PluginManager manager = new DefaultPluginManager(Paths.get("ourPlugins"));
        manager.loadPlugins();
        manager.startPlugins();
        Assert.assertEquals(1, manager.getStartedPlugins().size());
    }

    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("ourPlugins"));
        manager.loadPlugins();
        manager.startPlugins();
        System.out.println(manager.getExtensionClassNames("FileImporter"));
        System.out.println(manager.getExtensions(de.opendiabetes.vault.plugin.importer.FileImporter.class));

    }

}
