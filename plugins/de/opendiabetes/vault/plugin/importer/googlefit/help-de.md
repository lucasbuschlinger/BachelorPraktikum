# GoogleFitCSVImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der GoogleFitCSVImporter ist ein Importer-Plugin, das Fitness-Daten von Google Fit im CSV-Format importiert.

Daten
-----
Daten können von https://takeout.google.com/ exportiert werden, indem Sie Fit auswählen und auf Archiv erstellen klicken. Die Daten für den GoogleFitCSVImporter sind in all denjenigen Dateien, die in dem Archiv unter Takeout/Fit/Daily Aggregations zu finden sind und dem Format "2018-03-17.csv" entsprechen.

Konfiguration
-----
Das GoogleFitCSVImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem GoogleFitCSVImporter kompatibel sind. | x
| delimiter | ; | Das Trennzeichen, das beim Lesen der CSV-Datei verwendet werden soll. | 

Benötigte Plugins
-----
Der GoogleFitCSVImporter benötigt keine anderen Plugins.