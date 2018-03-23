# PlotterExporter
ver 0.0.1
Classification: Exporter

Overview
-----
The PlotterExporter is an exporter plugin which exports data from the database. It is responsible for the provision of data for the plotter. After the data was exported to a valid CSV file using the CSVExporter, the file is used to create plot from the data. After the process is finished the exported CSV file will be deleted.

It is possible to export the data into a PNG or PDF file. 
In order for the exporter to work the environment needs to have [Python 2.3](https://www.python.org/download/releases/2.3/) installed.
If you want to export the plot in a PDF file, the environment needs [pdflatex](https://www.latex-project.org/get/#tex-distributions) to be installed additionally.

Data example
-----

Configuration
-----
The PlotterExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the PlotterExporter plugin, separated by commas. | x |
| plotFormat | pdf, img | Specifies in which format the plotter exporter should export the data. |  |

Required Plugins
-----
The PlotterExporter does not require any other plugins.


 