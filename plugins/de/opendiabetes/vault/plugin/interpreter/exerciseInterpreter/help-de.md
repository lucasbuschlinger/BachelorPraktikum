# ExerciseInterpreter
Version: 1.0.0  
Klassifikation: Interpreter

Übersicht
-----
Der ExerciseInterpreter ist ein Interpreter-Plugin, das Aktivitätsdaten interpretiert, indem es Aktivitäten in verschiedene Typen filtert.

Konfiguration
-----
Das ExerciseInterpreter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem ExerciseInterpreter kompatibel sind. | x
| activityThreshold | 10 | Der Aktivitätsgrenzwert, der überschritten sein muss, damit ein Eintrag während der Interpretation nicht verworfen wird. | 
| activitySliceThreshold | 10 | Die maximale Länge eines Slices, in Minuten. |

Benötigte Plugins
-----
Der ExerciseInterpreter benötigt keine anderen Plugins.