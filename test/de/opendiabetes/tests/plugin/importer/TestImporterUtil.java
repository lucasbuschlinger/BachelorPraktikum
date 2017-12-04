package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.importer.Importer;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;

public class TestImporterUtil {
    public static Importer getImporterFromPath(String path){
        PluginManager manager = new DefaultPluginManager();
        manager.loadPlugin(Paths.get(path));
        manager.startPlugins();
        return manager.getExtensions(Importer.class).get(0);
    }
}
