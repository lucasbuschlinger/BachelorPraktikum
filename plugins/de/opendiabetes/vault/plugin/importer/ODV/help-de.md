# ODVImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der ODVImporter ist ein Importer-Plugin, das Daten aus ODV-ZIP-Archiven importiert. Diese Archive sind Ergebnisse des ODVExporters.

Konfiguration
-----
Das ODVImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem ODVImporter kompatibel sind. | x
| temporaryDirectory | "/tmp/odv/" | Das temporäre Verzeichnis, das beim Importieren verwendet wird. | 
| metaFile | "meta-2.info" | Der Name der Metadatei, die beim Importieren verwendet wird. (Standardwert: meta.info) | 

Benötigte Plugins
-----
Der ODVImporter benötigt eine Liste von Plugins, die implizit von den zu importierenden Daten gesetzt ist.