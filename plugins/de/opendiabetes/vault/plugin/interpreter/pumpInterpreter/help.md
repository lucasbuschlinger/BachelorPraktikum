# PumpInterpreter
Version: 0.0.1  
Classification: Interpreter

Overview
-----
The PumpInterpreter is a plugin which interprets collected pump data by filtering for example relevant events when the canula was filled.

Configuration
-----
The PumpInterpreter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| outputFilterSize | 10 | The size of the slidingWindow output filter. (Default: 5) | 
| fillCanulaAsNewKatheder | true | Boolean to say whether the canula has to be filled as a new Katheder. |
| fillCanulaCooldown | 10 | The time the Canula needs to cool down in minutes. |

Required Plugins
-----
 - The PumpInterpreter does not require any other plugins.