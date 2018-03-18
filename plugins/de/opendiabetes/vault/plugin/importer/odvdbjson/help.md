# ODVDBJsonImporter
Version: 0.0.1
Classification: Importer

Overview
-----
The ODVDBJsonImporter plugin is an importer plugin which imports ODVDB data in the .json format.

Data example
-----
The data needed by the ODVDBJsonImporter can be got from the Vault, i.e. by using the ODVDBJsonExporter and using its results.
Configuration
-----
The ODVDBJsonImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVDBJsonImporter plugin, separated by commas. | x

Required Plugins
-----
 - The ODVDBJsonImporter does not require any other plugins.