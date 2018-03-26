# SourceCodeExporter
Version 0.0.1  
Classification: Exporter

Overview
-----
The SourceCodeExporter is an exporter plugin which exports data from the database to Java source code.

Data example
-----

Configuration
-----
The SourceCodeExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the SourcecodeExporter plugin, separated by commas. | x
| periodRestriction | true | Whether there should be a period description from when to when the exported data should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 


Required Plugins
-----
The SourceCodeExporter does not require any other plugins.


 