# SliceLayoutCSVExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der SliceLayoutCSVExporter ist ein Exporter-Plugin, das Daten aus der Datenbank als sogenannte Slice-CSV-Dateien exportiert.
Diese Slices können später von Interpreter-Plugins verwendet werden, um Teile von Daten einzeln zu interpretieren.

Beispieldatensatz
-----

Konfiguration
-----
Das SliceLayoutCSVExporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem SliceLayoutCSVExporter kompatibel sind. | x
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der SliceLayoutCSVExporter benötigt keine anderen Plugins.