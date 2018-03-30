# LibreTextImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The LibreTextImporter plugin is an importer plugin, which imports LibreText data in CSV format from a .txt file.

Data
-----
In order to get LibreText data, visit http://www.freestylelibre.de/ and download the app. You can then use the app to export data for the LibreTextImporter.

Configuration
-----
The LibreTextImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the LibreTextImporter plugin, separated by commas. | x
| delimiter | ; | Delimiter to be used when reading the CSV file. | 

Required Plugins
-----
The LibreTextImporter does not require any other plugins.