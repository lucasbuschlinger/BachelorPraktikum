# SliceLayoutCSVExporter
Version 0.0.1  
Classification: Exporter

Overview
-----
The SliceLayoutCSVExporter is an exporter plugin which exports so named Slice CSV files which are representing slices of data from the database.
These slices can later be used for interpreter plugins to interpret only slices of data one by one.

Data example
-----

Configuration
-----
The SliceLayoutCSVExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the SliceLayoutCSVExporter plugin, separated by commas. | x
| periodRestriction | true |Whether there is a period description in which the exported data range should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 


Required Plugins
-----
The SliceLayoutCSVExporter does not require any other plugins.


 