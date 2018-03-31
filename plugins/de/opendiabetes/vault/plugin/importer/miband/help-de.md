# MiBandNotifyImporter
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der MiBandNotifyImporter ist ein Importer-Plugin, das Daten vom MiBand 2 importiert. Die zu importierenden Daten sind im JSON-Format.

Daten
-----
Um Daten für den MiBandNotifyImporter zu bekommen, kann man unter https://play.google.com/store/apps/details?id=com.mc.miband1&hl=en die App herunterladen. Mithilfe dieser erhält man Daten für den MiBandNotifyImporter.

Konfiguration
-----
Das MiBandNotifyImporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem MiBandNotifyImporter kompatibel sind. | x
| heartRateLowerBound | 45 | Der Wert für die untere Grenze der Herzschlagrate. (Standardwert: 40) | 
| heartRateUpperBound | 260 | Der Wert für die obere Grenze der Herzschlagrate. (Standardwert: 250) | 
| exerciseHeartThresholdMid | 100 | Der Wert der Herzschlagrate, ab der der Anstrengungsgrad einer Übung als mittel eingestuft wird.(Standardwert: 90) | 
| exerciseHeartThresholdHigh | 140 | Der Wert der Herzschlagrate, ab der der Anstrengungsgrad einer Übung als hoch eingestuft wird. (Default: 130) | 
| maxTimeGapMinutes | 5 | Gibt an, wie viele Minuten Einträge auseinander sein können, um beim Importieren noch zusammengefasst zu werden. (Default: 10) | 

Benötigte Plugins
-----
Der MiBandNotifyImporter benötigt keine anderen Plugins.