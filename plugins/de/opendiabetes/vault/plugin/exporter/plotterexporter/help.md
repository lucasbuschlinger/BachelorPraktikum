# PlotterExporter
Version: 1.0.0  
Classification: Exporter

Overview
-----
The PlotterExporter is an exporter plugin, which exports data from the database. It is responsible for the provision of data for the plotter. After the data was exported to a valid CSV file using the VaultCSVExporter, the file is used to create a plot from the data. After the process is finished, the exported CSV file will be deleted.

It is possible to export the data into a PNG or PDF file. 
In order for the exporter to work, the environment needs to have [Python 2.3](https://www.python.org/download/releases/2.3/) installed.
Additionally, the following libraries are required in the given version:

- [matplotlib (2.0.0)](https://matplotlib.org/2.0.0/index.html)
- [configparser](https://docs.python.org/2/library/configparser.html)

Moreover, [pdflatex](https://www.latex-project.org/get/#tex-distributions) needs to be installed in the environment if you want to export the plot in a PDF file.

Data example
-----
![Plotter example](https://i.imgur.com/TijPvyA.png)

Configuration
-----
The PlotterExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the PlotterExporter plugin, separated by commas. | x |
| plotFormat | pdf, img | Specifies in which format the plotter exporter should export the data. |  |
| periodRestriction | true | Whether there is a period description in which the exported data range should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 


Required Plugins
-----
The PlotterExporter requires the VaultCSVExporter.


 