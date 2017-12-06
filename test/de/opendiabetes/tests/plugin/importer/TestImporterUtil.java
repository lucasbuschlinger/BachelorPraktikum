package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.importer.Importer;
import org.junit.Assert;
import org.junit.Ignore;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;

@Ignore
public class TestImporterUtil {
    public static Importer getImporter(String importer){
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin(importer);
        Assert.assertEquals(1, manager.getStartedPlugins().size());
        Assert.assertEquals(1, manager.getExtensions(Importer.class).size());
        return manager.getExtensions(Importer.class).get(0);

    }
}
