# ODVExporter
Version: 1.0.0  
Classification: Exporter

Overview
-----
The ODVExporter is an exporter plugin which exports data from the database by using all other available exporter plugins and packing the exports of these into a zip-archive.

Configuration
-----
The ODVExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVExporter plugin, separated by commas. | x
| temporaryDirectory | /tmp/ODVExporter | Temporary directory to use while exporting. | 

Required Plugins
-----
The exporter uses all other available exporter plugins to export the data. Therefore all plugins which export to files you want in the exported ZIP-archive are required.