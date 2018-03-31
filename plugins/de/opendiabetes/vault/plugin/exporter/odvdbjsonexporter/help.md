# ODVDBJSONExporter
Version: 1.0.0  
Classification: Exporter

Overview
-----
The ODVDBJSONExporter is an exporter plugin, which exports data from the database into a JSON file.

Data example
-----
The exported data will be put into a file which contains valid JSON content.
```
[
    {
        "tp": 13,
        "ts": 1267401600000,
        "v1": 243.0,
        "v2": -5.0,
        "at": "[{\"t\":12,\"v\":\"\"}]"
    }
]
```

Configuration
-----
The ODVDBJSONExporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the ODVDBJSONExporter plugin, separated by commas. | x
| periodRestriction | true | Whether there is a period description in which the exported data range should be. (Default: false) | 
| periodRestrictionFrom | 24/04/2015 | Beginning date of the export period restriction. Required if periodRestriction is true. | 
| periodRestrictionTo | 24/05/2015 | End date of the export period restriction. Required if periodRestriction is true. | 

Required Plugins
-----
The ODVDBJSONExporter does not require any other plugins.


 