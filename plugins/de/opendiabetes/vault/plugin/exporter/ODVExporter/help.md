# ODVExporter
Version: 1.0.0  
Classification: Exporter

Overview
-----
The ODVExporter is an exporter plugin, which exports data from the database by using all other available exporter plugins and packing the exports of these into a zip-archive.

Configuration
-----
The ODVExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVExporter plugin, separated by commas. | x
| temporaryDirectory | /tmp/ODVExporter | Temporary directory to use while exporting. | 
| periodRestriction | true | Whether there is a period description in which the exported data range should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 

Required Plugins
-----
The ODVExporter uses all other available exporter plugins to export the data. Therefore, all plugins that export to files you want in the exported ZIP-archive are required.