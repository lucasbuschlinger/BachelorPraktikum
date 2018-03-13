package de.opendiabetes.tests.plugin.importer;

import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Ignore;

@Ignore
public class TestImporterUtil {
    /**
     * Returns an instance of the specified importer.
     * @param importer The type of the importer to be returned.
     * @return An instance of the specified importer.
     */
    public static Importer getImporter(final String importer) {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        return manager.getPluginFromString(Importer.class, importer);
    }

    /**
     * Returns an instance of the specified exporter.
     * @param exporter The type of the importer to be returned.
     * @return An instance of the specified exporter.
     */
    public static Exporter getExporter(final String exporter) {
        OpenDiabetesPluginManager manager = OpenDiabetesPluginManager.getInstance();
        return manager.getPluginFromString(Exporter.class, exporter);
    }
}
