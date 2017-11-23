package de.opendiabetes.vault.plugin.importer;

import de.opendiabetes.vault.importer.Importer;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;


public class FileImportTest {
    @Test
    public void pluginLoad(){
        PluginManager manager = new DefaultPluginManager(Paths.get("/home/magnus/OpenDiabetes/ourPlugins"));
        manager.loadPlugins();
        Assert.assertEquals(1,manager.getPlugins().size());
    }

    @Test
    public void pluginStart(){
        PluginManager manager = new DefaultPluginManager(Paths.get("/home/magnus/OpenDiabetes/ourPlugins"));
        manager.loadPlugins();
        manager.startPlugins();
        Assert.assertEquals(1,manager.getStartedPlugins().size());
    }

   @Test
    public void callPlugin(){
       PluginManager manager =new DefaultPluginManager(Paths.get("/home/magnus/OpenDiabetes/ourPlugins"));
       manager.loadPlugins();
       manager.startPlugins();
       System.out.println(manager.getExtensionClassNames("FileImporter"));
       System.out.println(manager.getExtensions(de.opendiabetes.vault.plugin.importer.FileImporter.class));

   }

}
