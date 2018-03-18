# GoogleFitCSVImporter
Version: 0.0.1
Classification: Importer

Overview
-----
The GoogleFitCSVImporter plugin is an importer plugin which imports Google Fit data in the .csv format.

Data example
-----
Data can be exported from https://takeout.google.com/ by selecting 'Fit' data. The data to import can then be found under Takeout/Fit/Daily Aggregations (in the downloaded archive file) and is either one of the files labeled with a date, such as "2018-03-17.csv".

Configuration
-----
The GoogleFitCSVImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the GoogleFitCSVImporter plugin, separated by commas. | x

Required Plugins
-----
 - The GoogleFitCSVImporter does not require any other plugins.