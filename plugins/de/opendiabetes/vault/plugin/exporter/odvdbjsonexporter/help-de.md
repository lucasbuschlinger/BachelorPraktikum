# ODVDBJSONExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der ODVDBJSONExporter ist ein Exporter-Plugin, das Daten aus der Datenbank als JSON Datei exportiert.

Beispieldatensatz
-----
Die exportierten Daten werden in eine Datei geschrieben, deren Inhalt JSON entspricht.
```
[
    {
        "tp": 13,
        "ts": 1267401600000,
        "v1": 243.0,
        "v2": -5.0,
        "at": "[{\"t\":12,\"v\":\"\"}]"
    }
]
```

Konfiguration
-----
Das ODVDBJSONExporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem ODVDBJSONExporter kompatibel sind. | x
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der ODVDBJSONExporter benötigt keine anderen Plugins.