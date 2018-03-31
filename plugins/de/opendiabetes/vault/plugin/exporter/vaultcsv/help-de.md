# VaultCSVExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der VaultCSVExporter ist ein Exporter-Plugin, das Daten aus der Datenbank als CSV-Dateien exportiert.

Beispieldatensatz
-----
[Standard-CSV-Format](https://de.wikipedia.org/wiki/CSV_(Dateiformat))
```
date,time,bgValue,cgmValue,
01.03.10,01:00,,
```

Konfiguration
-----
Das VaultCSVExporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem VaultCSVExporter kompatibel sind. | x
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der VaultCSVExporter benötigt keine anderen Plugins.