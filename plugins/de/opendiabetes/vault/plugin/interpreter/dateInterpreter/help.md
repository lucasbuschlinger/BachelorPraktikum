# DateInterpreter
Version: 1.0.0  
Classification: Interpreter

Overview
-----
The DateInterpreter is an interpreter plugin, which interprets VaultEntry data so that only entries that lie in the specified import period remain.

Configuration
-----
The DateInterpreter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the DateInterpreter plugin, separated by commas. | x
| importPeriodRestricted | false | Boolean indicating if a period of dates should be restricted to interpret. | 
| importPeriodFrom | "16.04.2015" | Start date from when the import period should begin. |
| importPeriodTo | "18.04.2015" | End date until when the import period should go. | 

Required Plugins
-----
The DateInterpreter does not require any other plugins.