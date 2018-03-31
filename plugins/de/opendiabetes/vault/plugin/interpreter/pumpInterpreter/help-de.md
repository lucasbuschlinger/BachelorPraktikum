# PumpInterpreter
Version: 1.0.0  
Klassifikation: Interpreter

Übersicht
-----
Der PumpInterpreter ist ein Interpreter-Plugin, das gesammelte Pumpendaten interpretiert, indem es beispielsweise relevante Ereignisse filtert, während die Kanüle voll war.

Konfiguration
-----
Das PumpInterpreter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem PumpInterpreter kompatibel sind. | x
| outputFilterSize | 10 | Die Größe des slidingWindow Output-Filters. (Standardwert: 5) | 
| fillCanulaAsNewCatheter | true | Gibt an, ob die Kanüle als neuer Katheter gefüllt werden muss. |
| fillCanulaCooldown | 10 | Die Wartezeit der Kanüle bis sie wieder verfügbar ist in Minuten. |

Benötigte Plugins
-----
Der PumpInterpreter benötigt keine anderen Plugins.