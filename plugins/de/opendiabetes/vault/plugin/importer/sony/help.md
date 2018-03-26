# SonySWR12Importer
Version: 0.0.1  
Classification: Importer

Overview
-----
The SonySWR12Importer plugin is an importer plugin which imports SonySWR12 data in the .csv format.

Data example
-----
The .csv format is commonly used to represent multivariate data. Every data point's values are separated by a delimiter. However, this delimiter is not standardized which means that it can vary between files. You can find more information on the .csv format here: https://en.wikipedia.org/wiki/Comma-separated_values

Configuration
-----
The SonySWR12Importer plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the SonySWR12Importer plugin, separated by commas. | x
| delimiter | ; | Delimiter to be used when reading the CSV file. | 

Required Plugins
-----
 - The SonySWR12Importer does not require any other plugins.