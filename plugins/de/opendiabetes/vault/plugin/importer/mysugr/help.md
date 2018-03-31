# MySugrCSVImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The MySugrCSVImporter plugin is an importer plugin, which imports MySugr data in the .csv format.

Data 
-----
To get MySugr data to import, simply open its app and select reports, then choose the .csv format. You can find more information on this at https://mysugr.com/how-to-use-mysugr-logbook-reports/ 

Configuration
-----
The MySugrCSVImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the MySugrCSVImporter plugin, separated by commas. | x
| delimiter | ; | Delimiter to be used when reading the CSV file. | 

Required Plugins
-----
The MySugrCSVImporter does not require any other plugins.