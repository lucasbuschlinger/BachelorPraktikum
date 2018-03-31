# MedtronicCrawlerImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The MedtronicCrawlerImporter is an importer plugin, which crawls the Medtronic CareLink page. 
You will need to have a CareLink account in order to access the data.

Data
-----
You can find more information on Medtronic data at https://carelink.minimed.com/patient/entry.jsp?bhcp=1

Configuration
-----
The MedtronicCrawlerImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the MedtronicCrawlerImporter plugin, separated by commas. | x
| fromDate | 15.06.2017 | Date from when the data should start to be imported | x
| toDate | 15.07.2017 | Date till when the data should be imported | x


Required Plugins
-----
The MedtronicCrawlerImporter requires the MedtronicImporter.