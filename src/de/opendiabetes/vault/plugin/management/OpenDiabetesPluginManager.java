/**
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.management;

import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import org.pf4j.DefaultPluginManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class is intended to simplify the interaction with all the plugins used in OpenDiabetes.
 *
 * @author magnus
 */
public class OpenDiabetesPluginManager {

    /**
     * The path to the configuration files of the plugins.
     */
    private Path configurationPath;

    /**
     * internal plugin manager provided by pf4j.
     */
    private DefaultPluginManager pf4jManager;

    /**
     * A map containing all compatible plugins for each plugin individually.
     */
    private Map<String, Set<String>> compatibilityMap = new HashMap<>();

    /**
     * map holding all loaded plugins.
     */
    private Map<String, OpenDiabetesPlugin> plugins = new HashMap<>();

    /**
     * Most generic Constructor.
     *
     * @param pluginPath        the path where all the plugins are located.
     * @param configurationPath the path where all the configuration files of the plugins are located.
     */
    public OpenDiabetesPluginManager(final Path pluginPath, final Path configurationPath) {
        this.configurationPath = configurationPath;
        pf4jManager = new DefaultPluginManager(pluginPath);
        pf4jManager.loadPlugins();
        pf4jManager.startPlugins();
        pf4jManager.getExtensions(OpenDiabetesPlugin.class).forEach(plugin -> plugins.put(pluginToString(plugin), plugin));

        pluginsLoadConfig();
        computeCompatibilityMap();
    }

    /**
     * Default Constructor.
     */
    public OpenDiabetesPluginManager() {
        this(Paths.get("export"), Paths.get("properties"));
    }

    /**
     * Initialises {@link this#compatibilityMap} from all compatibility lists of the plugins.
     * Make sure that load config was called on the plugins before,
     * otherwise {@link OpenDiabetesPlugin#getListOfCompatiblePluginIDs()} will return an empty list.
     */
    private void computeCompatibilityMap() {
        for (OpenDiabetesPlugin plugin : plugins.values()) {
            String pluginString = pluginToString(plugin);
            List<String> filteredCompatibility = plugin.getListOfCompatiblePluginIDs()
                    .stream()
                    .filter(y -> pf4jManager.getExtensions(y).size() != 0) //list only compatible plugins that are available
                    .collect(Collectors.toList());
            Set<String> compatibilitySet = compatibilityMap.get(pluginString);
            if (compatibilitySet == null) { //create set if it does not exist
                compatibilitySet = new TreeSet<>();
            }
            compatibilitySet.addAll(filteredCompatibility);
            compatibilityMap.put(pluginString, compatibilitySet);

            for (String compatiblePlugin : filteredCompatibility) {
                Set<String> newEntry = compatibilityMap.get(compatiblePlugin);
                if (compatibilityMap.get(compatiblePlugin) == null) {
                    newEntry = new TreeSet<>();
                }
                newEntry.add(pluginString);
                compatibilityMap.put(compatiblePlugin, newEntry);
            }
        }
    }

    /**
     * @param plugin the plugin whos base path is returned
     * @return the base path of the plugin
     */
    private String getPluginBasePath(final OpenDiabetesPlugin plugin) {
        //return plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        return pf4jManager.getPlugin(pluginToString(plugin)).getPluginPath().toString();
    }

    /**
     * @param plugin the plugin that is compatible with all the plugins returned
     * @return a list of all plugins that are compatible with the provided one
     */
    public Set<String> getCompatiblePlugins(final OpenDiabetesPlugin plugin) {
        return compatibilityMap.get(pluginToString(plugin));
    }

    /**
     * Calls setPluginPropertiesFromDefaultPath on every plugin,
     * thus every plugin has a configuration set afterwards if the necessary configuration files were found.
     */
    private void pluginsLoadConfig() {
        getPluginsOfType(OpenDiabetesPlugin.class).forEach(plugin -> setPluginPropertiesFromDefaultPath(plugin));
    }

    /**
     * Takes a plugin and calls loadConfiguration with the configuration file found at the default path.
     *
     * @param plugin the plugin to load the configuration for
     */
    private void setPluginPropertiesFromDefaultPath(final OpenDiabetesPlugin plugin) {
        Properties config = new Properties();
        FileInputStream input = null;
        try {
            String filename = pluginToString(plugin) + ".properties";
            Path defaultConfigFilePath = Paths.get(getPluginBasePath(plugin), filename);
            Path configFilePath = Paths.get(this.configurationPath.toString(), filename);
            if (!Files.exists(configFilePath)) {
                Files.copy(defaultConfigFilePath, configFilePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("copying default configuration of " + pluginToString(plugin));
            }
            input = new FileInputStream(Paths.get(configurationPath.toString(), filename).toFile());
            config.load(input);
            plugin.loadConfiguration(config);
        } catch (IOException e) {
            e.printStackTrace();
            //Assert.fail("path to configuration not found");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param type the type of Plugins you want to recieve (eg Importer.class, Exporter.class, ...)
     * @param <T>  The type of Plugins specified by type
     * @return a list of all available Plugins of the specified type
     */
    public <T extends OpenDiabetesPlugin> List<T> getPluginsOfType(final Class<T> type) {
        return plugins.values()
                .stream()
                .filter(plugin -> type.isInstance(plugin))
                .map(plugin -> (T) plugin)
                .collect(Collectors.toList());
    }

    /**
     * Takes a plugin and returns the pluginID.
     *
     * @param plugin the plugin of which the pluginID is returned
     * @return the pluginID of the plugin
     */
    public String pluginToString(final OpenDiabetesPlugin plugin) {
        return plugin.getClass().getEnclosingClass().getSimpleName();
    }

    /**
     * @param plugins a list of Plugins
     * @return a list of the names of the Plugins
     */
    public List<String> pluginsToStringList(final Collection<? extends OpenDiabetesPlugin> plugins) {
        return plugins.stream().map(x -> pluginToString(x)).collect(Collectors.toList());
    }

    /**
     * @param pluginIDs a list of plugin IDs
     * @return a set of the plugins
     */
    public List<OpenDiabetesPlugin> pluginsFromStringList(final Collection<String> pluginIDs) {
        return pluginIDs
                .stream()
                .map(pluginID -> getPluginFromString(OpenDiabetesPlugin.class, pluginID))
                .collect(Collectors.toList());
    }

    /**
     * @param type     the class of the plugin
     * @param pluginID the name of the plugin
     * @param <T>      the type of the plugin specified in type
     * @return the plugin <<pluginID>> with type <<type>>
     */
    public <T extends OpenDiabetesPlugin> T getPluginFromString(final Class<T> type, final String pluginID) {
        OpenDiabetesPlugin plugin = plugins.get(pluginID);
        if (type.isInstance(plugin)) {
            return (T) plugin;
        }
        return null;
    }
}
