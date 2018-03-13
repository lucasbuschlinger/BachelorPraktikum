# LibreTextImporter
Version: 0.0.1
Classification: Importer

Overview
-----
The LibreTextImporter plugin is an importer plugin which imports LibreText data in the .csv format.

Data example
-----
The .csv format is commonly used to represent multivariate data. Every data point's values are separated by a delimiter. However, this delimiter is not standardized which means that it can vary between files. You can find more information on the .csv format here: https://en.wikipedia.org/wiki/Comma-separated_values

Configuration
-----
The LibreTextImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the LibreTextImporter plugin, separated by commas. | x

Required Plugins
-----
 - The LibreTextImporter does not require any other plugins.