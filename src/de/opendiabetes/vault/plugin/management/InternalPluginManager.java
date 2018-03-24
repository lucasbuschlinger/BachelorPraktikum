package de.opendiabetes.vault.plugin.management;

import org.pf4j.CompoundPluginLoader;
import org.pf4j.DefaultPluginManager;
import org.pf4j.JarPluginLoader;
import org.pf4j.PluginLoader;

import java.nio.file.Path;

/**
 * Extension upon the {@link DefaultPluginManager} as a workaround to having multiple copies of libraries across different plugins.
 *
 * @author Lucas Buschlinger
 */
public class InternalPluginManager extends DefaultPluginManager {

    /**
     * Constructor matching super.
     * @param pluginsRoot The folder where all plugins reside in.
     */
    public InternalPluginManager(final Path pluginsRoot) {
        super(pluginsRoot);
    }

    /**
     * Creator for the plugin loader.
     * Uses the {@link OpenDiabetesPluginLoader} instead of the {@link org.pf4j.DefaultPluginLoader}.
     * @return The pluginLoader.
     */
    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new OpenDiabetesPluginLoader(this, pluginClasspath))
                .add(new JarPluginLoader(this));
    }
}
