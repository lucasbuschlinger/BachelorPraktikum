package de.opendiabetes.vault.plugin.management;

import org.pf4j.DefaultPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginClasspath;
import org.pf4j.PluginManager;
import org.pf4j.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Extension to the {@link DefaultPluginLoader} as a workaround to having multiple copies of libraries across different plugins.
 *
 * @author Lucas Buschlinger
 */
public class OpenDiabetesPluginLoader extends DefaultPluginLoader {

    /**
     * Constructor matching super in {@link DefaultPluginLoader}.
     * @param pluginManger The {@link PluginManager} to use.
     * @param pluginClasspath The {@link PluginClasspath} where the plugins lie.
     */
    public OpenDiabetesPluginLoader(final PluginManager pluginManger, final PluginClasspath pluginClasspath) {
        super(pluginManger, pluginClasspath);
    }

    /**
     * Method to load the libraries used by the plugins.
     * This version only loads the library if it does not exist in the projects library.
     *
     * @param pluginPath The path to the plugin.
     * @param pluginClassLoader The {@link PluginClassLoader} to use.
     */
    @Override
    protected void loadJars(final Path pluginPath, final PluginClassLoader pluginClassLoader) {
        for (String libDirectory : pluginClasspath.getLibDirectories()) {
            Path file = pluginPath.resolve(libDirectory);
            List<File> jars = FileUtils.getJars(file);
            for (File jar : jars) {
                if (!jarInLibs(jar)) {
                    pluginClassLoader.addFile(jar);
                }
            }
        }
    }

    /**
     * Checks whether the given file exists in the projects library folder.
     *
     * @param jar The jar/file to check for.
     * @return True if the jar/file exists in the library folder, false otherwise.
     */
    private boolean jarInLibs(final File jar) {
        String libraryPath = "lib";
        String jarName = jar.getName();
        Path library = Paths.get(libraryPath + File.separator + jarName);
        File checkFile = library.toFile();
        return checkFile.exists();
    }
}
