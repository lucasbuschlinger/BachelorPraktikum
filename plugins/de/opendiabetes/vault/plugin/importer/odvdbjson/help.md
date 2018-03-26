# ODVDBJSONImporter
Version: 0.0.1  
Classification: Importer

Overview
-----
The ODVDBJSONImporter plugin is an importer plugin which imports ODVDB data in the .json format.

Data
-----
The data needed by the ODVDBJSONImporter can be fetched from the Vault, i.e. by using the ODVDBJSONExporter and using its results.

Configuration
-----
The ODVDBJSONImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVDBJSONImporter plugin, separated by commas. | x

Required Plugins
-----
 - The ODVDBJSONImporter does not require any other plugins.