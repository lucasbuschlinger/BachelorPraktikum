# GoogleFitCrawlerImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der GoogleFitCrawlerImporter ist ein Importer-Plugin, das zum Importieren von Daten auf die Google API zugreift.
Um diesen Importer zu nutzen, muss ein API-Schlüssel erstellt werden.

Wie man einen API-Schlüssel bekommt:

1. Gehen Sie auf [developers.google.com/console](https://developers.google.com/console) und melden Sie sich mit einem Google-Konto an.
2. Wählen Sie eins Ihrer existierenden Projekte aus oder erstellen Sie ein neues Projekt.
3. Aktivieren Sie die folgenden APIs, um den Java Client benutzen zu können:
    - Fitness API
    - People API
    - Geocoding API
    - Places API
    - Google Maps JavaScript API
    - Google Static Maps API
4. Erstellen Sie einen API-Schlüssel, der von der People API verwendet wird.
5. Erstellen Sie des Weiteren eine OAuth 2.0 Client ID für die anderen APIs.
6. Falls nötig, schränken Sie Anfragen auf eine IP-Adresse ein.

Konfiguration
-----
Das GoogleFitCrawlerImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem GoogleFitCrawlerImporter kompatibel sind. | x
| apiKey | foo_bar | Der API-Schlüssel, den Sie wie oben beschrieben bekommen. | x
| clientSecretPath | ~/.credentials/client_secret.json | Der Pfad zu den Client-Anmeldedaten. | x
| age | 25 | Das Alter des Benutzers. | x
| timeframe | 15.06.2017-15.07.2017 oder all | Der Zeitraum, aus dem Daten gesammelt werden sollen. |
| exportHistory | true | Gibt an, ob die Datenhistorie exportiert werden soll. |
| plotTimeframe | 15.06.2017-15.07.2017 | Der Zeitraum, aus dem Daten grafisch visualisiert werden sollen. |
| exportPlot | true | Gibt an, ob die Visualisierung der Daten exportiert werden soll. |
| viewPlot | true | Gibt an, ob die Visualisierung der Daten angezeigt werden soll. |
| viewMap | true | Gibt an, ob der Standortverlauf des Benutzers angezeigt werden soll. |

Benötigte Plugins
-----
Der GoogleFitCrawlerImporter benötigt keine anderen Plugins.