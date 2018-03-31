# ODVImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The ODVImporter is an importer plugin, which imports data from an ODV ZIP-Archive. This data archive can be exported with the ODVExporter.

Configuration
-----
The ODVImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVImporter plugin, separated by commas. | x
| temporaryDirectory | "/tmp/odv/" | The temporary directory to use. | 
| metaFile | "meta-2.info" | The name of the meta file to use. (Default: meta.info) | 

Required Plugins
-----
The ODVImporter requires a list of plugins which are implicitly set by the data to import. 