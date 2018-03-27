# MiBandNotifyImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The MiBandNotifyImporter is an importer plugin which imports data from the MiBand 2. The data to import is in a JSON format. 

Data
-----
To get MiBand 2 data for the import, visit https://play.google.com/store/apps/details?id=com.mc.miband1&hl=en

Configuration
-----
The MiBandNotifyImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the MiBandNotifyImporter plugin, separated by commas. | x
| heartRateLowerBound | 45 | The value for the lower bound of the heart rate. (Default: 40) | 
| heartRateUpperBound | 260 | The value for the upper bound of the heart rate. (Default: 250) | 
| exerciseHeartThresholdMid | 100 | The value for the threshold from where an exercise will be classed as medium demanding. (Default: 90) | 
| exerciseHeartThresholdHigh | 140 | The value for the threshold from where an exercise will be classed as highly demanding. (Default: 130) | 
| maxTimeGapMinutes | 5 | The value for the time span in which entries will be joined together. (Default: 10) | 

Required Plugins
-----
 - The MiBandNotifyImporter does not require any other plugins.