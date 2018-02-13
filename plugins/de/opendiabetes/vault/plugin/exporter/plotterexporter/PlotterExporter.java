package de.opendiabetes.vault.plugin.exporter.plotterexporter;

import de.opendiabetes.vault.container.SliceEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.exporter.CSVFileExporter;
import de.opendiabetes.vault.plugin.exporter.FileExporter;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import de.opendiabetes.vault.plugin.exporter.slicelayoutcsv.SliceLayoutCSVExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

/**
 * Wrapper class for the PlotterExporter plugin.
 *
 * @author ocastx
 */
public class PlotterExporter extends Plugin {

    /**
     * Constructor for the {@link org.pf4j.PluginManager}.
     *
     * @param wrapper The {@link org.pf4j.PluginWrapper}.
     */
    public PlotterExporter(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class PlotterExporterImplementation extends VaultExporter {

        private static boolean isCommandInstalled(String[] cmds, String check) {
            try {
                Process process = Runtime.getRuntime().exec(cmds);

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(process.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(process.getErrorStream()));

                // read the output from the command
                String s = null;
                boolean installed = false;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                    if (s.contains(check)) {
                        installed = true;
                        break;
                    }
                }

                if (!installed) {
                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }
                    return false;
                }

                return true;
            } catch (IOException e) {
                System.out.println("Error while checking for command " + check);
                return false;
            }
        }

        private static boolean isPythonInstalled() {
            return isCommandInstalled(new String[]{"python", "--version"}, "Python");
        }

        private static boolean isLaTeXInstalled() {
            return isCommandInstalled(new String[]{"pdflatex", "--version"}, "java");
        }

        private static boolean

        @Override
        protected void writeToFile(List<ExportEntry> csvEntries) throws IOException {

            boolean python = isPythonInstalled();
            boolean latex = isLaTeXInstalled();

            super.writeToFile(csvEntries);



        }

        /**
         * Loads the configuration for the exporter plugin.
         *
         * @param configuration The configuration object.
         * @return True if configuration can be loaded, false otherwise.
         */
        @Override
        public boolean loadConfiguration(Properties configuration) {
            return false;
        }
    }
}
