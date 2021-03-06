package de.opendiabetes.tests.plugin.management;

import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.importer.crawlerimporter.CrawlerImporter;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import de.opendiabetes.vault.plugin.util.HelpLanguage;
import org.junit.Test;

import java.util.Arrays;

public class OpenDiabetesPluginManagerTest {
    @Test
    public void printPluginListsTest() {
        OpenDiabetesPluginManager m = OpenDiabetesPluginManager.getInstance();
        System.out.println("Printing all plugins:");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(OpenDiabetesPlugin.class)).toArray()));
        System.out.println("Printing all Importers");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(CrawlerImporter.class)).toArray()));

        System.out.println("Printing all Exporters");
        System.out.println(
                Arrays.toString(
                        m.pluginsToStringList(m.getPluginsOfType(Exporter.class)).toArray()));
    }

    @Test
    public void printCompatibilityListTest() {
        OpenDiabetesPluginManager m = OpenDiabetesPluginManager.getInstance();
        m.getPluginsOfType(OpenDiabetesPlugin.class).forEach(plugin ->
        System.out.println(m.pluginToString(plugin) + ": " + Arrays.toString(
                 m.getCompatiblePluginIDs(plugin).toArray())));
    }

    @Test
    public void printGetHelpFilePath() {
        OpenDiabetesPluginManager m = OpenDiabetesPluginManager.getInstance();
        int defaultHelpCount = 0;
        for (OpenDiabetesPlugin plugin : m.getPluginsOfType(OpenDiabetesPlugin.class)) {
            try {
                System.out.println(m.pluginToString(plugin)+" "+m.getHelpFilePath(plugin, HelpLanguage.LANG_EN));
                if(m.getHelpFilePath(plugin, HelpLanguage.LANG_EN).toString().contains("help.md")){
                    defaultHelpCount++;
                }
            } catch (Exception exception) {}
        }
        System.out.println("\n"+defaultHelpCount+"/"+m.getPluginsOfType(OpenDiabetesPlugin.class).size()+" plugins do not provide a help file yet.");
    }
}
