# DateInterpreter
Version: 1.0.0  
Klassifikation: Interpreter

Übersicht
-----
Der DateInterpreter ist ein Interpreter-Plugin, das VaultEntry-Daten interpretiert, sodass nur Einträge aus dem festgelegten Zeitraum übrig bleiben.

Konfiguration
-----
Das DateInterpreter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem DateInterpreter kompatibel sind. | x
| importPeriodRestricted | false | Gibt an, ob nur Daten aus einem beschränkten Zeitraum interpretiert werden sollen. | 
| importPeriodFrom | "16.04.2015" | Start des Daten Zeitraums. Notwendig, wenn periodRestriction true ist. |
| importPeriodonTo | "18.04.2015" | Ende des Daten Zeitraum. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der DateInterpreter benötigt keine anderen Plugins.