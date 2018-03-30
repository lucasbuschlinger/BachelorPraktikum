# VaultCSVExporter
Version: 1.0.0  
Classification: Exporter

Overview
-----
The VaultCSVExporter is an exporter plugin, which exports data from the database into a CSV format.

Data example
-----
[Standard CSV format](https://en.wikipedia.org/wiki/Comma-separated_values)
```
date,time,bgValue,cgmValue,
01.03.10,01:00,,
```

Configuration
-----
The VaultCSVExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the VaultCSVExporter plugin, separated by commas. | x
| periodRestriction | true | Whether there is a period description in which the exported data range should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 

Required Plugins
-----
The VaultCSVExporter does not require any other plugins.


 