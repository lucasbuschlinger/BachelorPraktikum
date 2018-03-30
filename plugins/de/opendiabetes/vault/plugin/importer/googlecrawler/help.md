# GoogleFitCrawlerImporter
Version: 1.0.0  
Classification: Importer

Overview
-----
The GoogleFitCrawlerImporter is an importer plugin, which accesses the Google API. 
In order to get the importer up and running you'll need to create an API Key.

To get an API key:

1. Visit [developers.google.com/console](https://developers.google.com/console) and log in with a Google Account.
1. Select one of your existing projects, or create a new project.
1. Enable the following APIs to fully use the Java Client:
    - Fitness API
    - People API
    - Geocoding API
    - Places API
    - Google Maps JavaScript API
    - Google Static Maps API
1. Create and acquire an API key, which is used by the People API.
1. Furthermore, request an OAuth 2.0 client ID for the other APIs.
1. If needed restrict requests to a specific IP address.

Configuration
-----
The GoogleFitCrawlerImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | A list of plugins that are compatible with the GoogleFitCrawlerImporter plugin, separated by commas. | x
| apiKey | foo_bar | The Google API acquired above. | x
| clientSecretPath | ~/.credentials/client_secret.json | Path to the client credentials downloaded from Google. | x
| age | 25 | Age of the user. | x
| timeframe | 15.06.2017-15.07.2017 or all | The timeframe in which the data should be gathered. |
| exportHistory | true | Whether the data history should be exported. |
| plotTimeframe | 15.06.2017-15.07.2017 | The timeframe in which the data should be plotted. |
| exportPlot | true | Whether the data plot should be exported. |
| viewPlot | true | Whether the data plot should be shown. |
| viewMap | true | Whether the location history of the user should be shown. |


Required Plugins
-----
The GoogleFitCrawlerImporter does not require any other plugins.