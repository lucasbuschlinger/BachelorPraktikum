# PlotterExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der PlotterExporter ist ein Exporter-Plugin, das Daten aus der Datenbank mithilfe des VaultCSVExporter Plugins exportiert und aus der erstellten Datei eine grafische Darstellung erzeugt. Anschließend wird die Datei wieder gelöscht.

Die Daten können sowohl als PNG-Datei als auch als PDF-Datei exportiert werden.  
Damit der Exporter funktioniert, muss [Python 2.3](https://www.python.org/download/releases/2.3/) installiert sein. 
Außerdem werden die folgenden Bibliotheken benötigt:

- [matplotlib (2.0.0)](https://matplotlib.org/2.0.0/index.html)
- [configparser](https://docs.python.org/2/library/configparser.html)

Für den Export als PDF-Datei ist es weiterhin notwendig, dass [pdflatex](https://www.latex-project.org/get/#tex-distributions) installiert ist.

Beispieldatensatz
-----
![Beispiel eines Graphen](https://i.imgur.com/TijPvyA.png)

Konfiguration
-----
Das PlotterExporter Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem PlotterExporter kompatibel sind. | x
| plotFormat | pdf, img | Gibt an, welches Dateiformat beim Exportieren gewünscht ist. | 
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der PlotterExporter benötigt den VaultCSVExporter.