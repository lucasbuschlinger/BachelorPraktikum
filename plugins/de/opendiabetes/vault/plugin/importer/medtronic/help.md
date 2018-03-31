# MedtronicImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The MedtronicImporter plugin is an importer plugin, which imports Medtronic data in the .csv format.

Data
-----
You can find more information on Medtronic data at https://carelink.minimed.com/patient/entry.jsp?bhcp=1

Configuration
-----
The MedtronicImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the MedtronicImporter plugin, separated by commas. | x
| delimiter | ; | Delimiter to be used when reading the CSV file. | 

Required Plugins
-----
The MedtronicImporter does not require any other plugins.