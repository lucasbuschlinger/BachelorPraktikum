# PumpInterpreter
Version: 1.0.0  
Klassifikation: Interpreter

Übersicht
-----
Der PumpInterpreter ist ein Interpreterplugin, das gesammelte Pumpendaten interpretiert, indem es beispielsweise relevante Events filtert, während die Kanüle voll war.

Konfiguration
-----
Das PumpInterpreter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem PumpInterpreter kompatibel sind. | x
| outputFilterSize | 10 | Die Größe des slidingWindow Output-Filters. (Standardwert: 5) | 
| fillCanulaAsNewKatheder | true | Gibt an, ob die Kanüle als neuer Katheter gefüllt werden muss. |
| fillCanulaCooldown | 10 | Die Wartezeit der Kanüle in Minuten. |

Benötigte Plugins
-----
Der PumpInterpreter benötigt keine anderen Plugins.