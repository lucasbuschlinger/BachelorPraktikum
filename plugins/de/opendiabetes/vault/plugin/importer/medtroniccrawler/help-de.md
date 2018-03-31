# MedtronicCrawlerImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der MedtronicCrawlerImporter ist ein Importer-Plugin, das Medtronic-Daten von der Webseite von Medtronic Carelink importiert.

Daten
-----
Informationen über Medtronic-Daten können unter https://carelink.minimed.com/patient/entry.jsp?bhcp=1 gefunden werden.

Konfiguration
-----
Das MedtronicCrawlerImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem MedtronicCrawlerImporter kompatibel sind. | x
| fromDate | 15.06.2017 | Das Datum, ab dem Daten importiert werden sollen. | x
| toDate | 15.07.2017 | Das Datum, bis zu dem Daten importiert werden sollen. | x

Benötigte Plugins
-----
Der MedtronicCrawlerImporter benötigt den MedtronicImporter.