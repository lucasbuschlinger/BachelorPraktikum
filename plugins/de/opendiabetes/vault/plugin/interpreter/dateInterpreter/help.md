# DateInterpreter
Version: 0.0.1
Classification: Interpreter

Overview
-----
The DateInterpreter is a plugin which interprets VaultEntry dates containing only the entries that are specified in the given import period.

Configuration
-----
The DateInterpreter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| ImportPeriodRestricted | false | Boolean indicating if a period of dates should be restricted to interpret. | 
| importPeriodFrom | "16.04.2015" | Start date from when the import period should begin. |
| importPeriodTo | "18.04.2015" | End date until when the import period should go. | 

Required Plugins
-----
 - The DateInterpreter does not require any other plugins.