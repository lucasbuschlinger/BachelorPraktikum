# MedtronicImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der MedtronicImporter ist ein Importer-Plugin, das Medtronic-Daten im CSV-Format importiert.

Daten
-----
Informationen über Medtronic-Daten können unter https://carelink.minimed.com/patient/entry.jsp?bhcp=1 gefunden werden.

Konfiguration
-----
Das MedtronicImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem MedtronicImporter kompatibel sind. | x
| delimiter | ; | Das Trennzeichen, das beim Lesen der CSV-Datei verwendet werden soll. | 

Benötigte Plugins
-----
Der MedtronicImporter benötigt keine anderen Plugins.