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
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        Assert.assertTrue(0 != manager.getPlugins().size());
    }

    @Test
    public void pluginStart() throws PluginException {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.enablePlugin("FileImporter");
        manager.startPlugins();
        Assert.assertTrue(manager.enablePlugin("FileImporter"));

    }

    @Test
    public void callPlugin() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugins();
        manager.getExtensions(Importer.class).forEach(importer->importer.importData("path/to/data"));
    }

}
