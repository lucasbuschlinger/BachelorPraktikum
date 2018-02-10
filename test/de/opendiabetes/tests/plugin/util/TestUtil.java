package de.opendiabetes.tests.plugin.util;

import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;
import org.junit.Assert;
import org.junit.Ignore;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;

@Ignore
public class TestUtil {
    /**
     * Returns an instance of the specified importer.
     * @param importer The type of the importer to be returned.
     * @return An instance of the specified importer.
     */
    public static Importer getImporter(final String importer) {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin(importer);
        Assert.assertEquals(1, manager.getStartedPlugins().size());
        Assert.assertEquals(
                "please delete out folder, most of the time" +
                        " default plugin loader finds plugins on default classpath that are not wanted",
                1, manager.getExtensions(Importer.class).size());
        return manager.getExtensions(Importer.class).get(0);
    }

    /**
     * Returns an instance of the specified exporter.
     * @param exporter The type of the importer to be returned.
     * @return An instance of the specified exporter.
     */
    public static Exporter getExporter(final String exporter) {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin(exporter);
        Assert.assertEquals(1, manager.getStartedPlugins().size());
        Assert.assertEquals(
                "please delete out folder, most of the time " +
                        "default plugin loader finds plugins on default classpath that are not wanted",
                1, manager.getExtensions(Exporter.class).size());
        return manager.getExtensions(Exporter.class).get(0);
    }

    /**
     * Returns an instance of the specified exporter.
     * @param interpreter The type of the importer to be returned.
     * @return An instance of the specified exporter.
     */
    public static Interpreter getInterpreter(final String interpreter) {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugin(interpreter);
        Assert.assertEquals(1, manager.getStartedPlugins().size());
        Assert.assertEquals("please delete out folder, most of the time " +
                "default plugin loader finds plugins on default classpath that are not wanted",
                1, manager.getExtensions(Interpreter.class).size());
        return manager.getExtensions(Interpreter.class).get(0);
    }
}
