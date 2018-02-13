package de.opendiabetes.vault.plugin.exporter.plotterexporter;

import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.*;
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
    public PlotterExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the PlotterExporter.
     */
    @Extension
    public static class PlotterExporterImplementation extends VaultExporter {

        /**
         * Supported plot formats.
         */
        private enum PlotFormats {

            /**
             * Image plot format.
             */
            IMAGE,

            /**
             * LaTeX PDF plot format.
             */
            PDF
        }

        /**
         * Selected plot format read from the configuration.
         */
        private PlotFormats plotFormat;

        /**
         * Runs the plot script.
         * @param dataPath path to the data that should be plotted
         * @param outputPath path where the plot should be written to
         * @return boolean value indicating whether the command was successful or not
         */
        private static boolean plotData(final String dataPath, final String outputPath) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"python", "ploty.py", "-f", dataPath, "-o", outputPath});

                InputStream inputStream = process.getInputStream();
                InputStream errorStream = process.getErrorStream();

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(inputStream));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(errorStream));

                // read the output from the command
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }

                // read any errors from the attempted command
                int errorCounter = 0;
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                    errorCounter++;
                }

                return (errorCounter == 0);
            } catch (IOException e) {
                System.out.println("Error while executing command");
                return false;
            }
        }

        /**
         * Checks if the given command outputs the expected value.
         * @param cmds array of command arguments that should be executed
         * @param check string value which will be used as check argument
         * @return boolean value indicating whether the command is installed or not
         */
        private static boolean isCommandInstalled(final String[] cmds, final String check) {
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
                    System.out.println("Could not check command with errors:\n");
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

        /**
         * Checks if python is installed on the current machine.
         * @return true if python is installed, false if not
         */
        private static boolean isPythonInstalled() {
            return isCommandInstalled(new String[]{"python", "--version"}, "Python");
        }

        /**
         * Checks if LaTeX is installed on the current machine.
         * @return true if LaTeX is installed, false if not
         */
        private static boolean isLaTeXInstalled() {
            return isCommandInstalled(new String[]{"pdflatex", "--version"}, "pdfTeX");
        }

        /**
         * Writes the data to a CSV file and calls the plotter script.
         *
         * @param csvEntries The {@link ExportEntry} to be exported.
         * @throws IOException Thrown if there was an error while writing or deleting files.
         */
        @Override
        protected void writeToFile(final List<ExportEntry> csvEntries) throws IOException {

            boolean python = isPythonInstalled();
            if (!python) {
                throw new IOException("Cannot plot data because python was not found");
            }

            String plotPath = "plot.jpg";
            if (plotFormat == PlotFormats.PDF) {
                boolean latex = isLaTeXInstalled();
                plotPath = "plot.pdf";

                if (!latex) {
                    throw new IOException("Cannot plot data to pdf file because pdflatex was not found");
                }
            }

            super.writeToFile(csvEntries);
            if (!plotData(this.getExportFilePath(), plotPath)) {
                System.out.println("Failed to plot data");
            }


            File file = new File(this.getExportFilePath());
            if (!file.delete()) {
                System.out.println("Failed to delete export file");
            }
        }

        /**
         * Loads the configuration for the exporter plugin.
         *
         * @param configuration The configuration object.
         * @return True if configuration can be loaded, false otherwise.
         */
        @Override
        public boolean loadConfiguration(final Properties configuration) {

            this.notifyStatus(0, "Loading configuration");

            String format = configuration.getProperty("plotFormat");

            if (format.equals("pdf")) {
                plotFormat = PlotFormats.PDF;
            } else {
                // Default is always "image"
                plotFormat = PlotFormats.IMAGE;
            }

            return false;
        }
    }
}
