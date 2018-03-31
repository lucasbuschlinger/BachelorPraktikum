# ODVExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der ODVExporter ist ein Exporter-Plugin, das Daten aus der Datenbank exportiert, indem die Daten mithilfe aller anderen verfügbaren Exporter Plugins exportiert und deren Exportdateien in ein ZIP-Archiv gepackt werden.

Beispieldatensatz
-----

Konfiguration
-----
Das ODVExporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem ODVExporter kompatibel sind. | x
| temporaryDirectory | /tmp/ODVExporter | Das temporäre Verzeichnis, das beim Exportieren verwendet wird. | 
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der ODVExporter benutzt alle anderen verfügbaren Exporter Plugins. Daher benötigt er alle Plugins, deren Exportdateien am Ende im ZIP-Archiv enthalten sein sollen.