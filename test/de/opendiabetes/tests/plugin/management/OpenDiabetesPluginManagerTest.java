package de.opendiabetes.tests.plugin.management;

import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Test;

import java.util.Arrays;

public class OpenDiabetesPluginManagerTest {
    @Test
    public void printPluginListsTest() {
        OpenDiabetesPluginManager m = new OpenDiabetesPluginManager();
        System.out.println("Printing all plugins:");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(OpenDiabetesPlugin.class)).toArray()));
        System.out.println("Printing all Importers");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(Importer.class)).toArray()));

        System.out.println("Printing all Exporters");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(Exporter.class)).toArray()));
    }

    @Test
    public void printCompatibilityListTest() {
        OpenDiabetesPluginManager m = new OpenDiabetesPluginManager();
        m.getPluginsOfType(OpenDiabetesPlugin.class).forEach(plugin ->
        System.out.println(m.pluginToString(plugin) + ": " + Arrays.toString(
                 m.getCompatiblePlugins(plugin).toArray())));
    }
}
