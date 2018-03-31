# PumpInterpreter
Version: 1.0.0  
Classification: Interpreter

Overview
-----
The PumpInterpreter is an interpreter plugin, which interprets collected pump data for example by filtering relevant events when the canula was filled.

Configuration
-----
The PumpInterpreter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the PumpInterpreter plugin, separated by commas. | x
| outputFilterSize | 10 | The size of the slidingWindow output filter. (Default: 5) | 
| fillCanulaAsNewCatheter | true | Boolean to say whether the canula has to be filled as a new catheter. |
| fillCanulaCooldown | 10 | The time the canula needs to cool down in minutes. |

Required Plugins
-----
The PumpInterpreter does not require any other plugins.