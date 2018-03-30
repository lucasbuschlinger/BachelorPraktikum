# SonySWR12Importer
Version: 1.0.0  
Klassifikation: Importer

Übersicht
-----
Der SonySWR12Importer ist ein Importer-Plugin, das SonySWR12-Daten im CSV-Format importiert.

Daten
-----
Daten für den SonySWR12Importer werden von der *SmartBand 2 SWR12* App generiert. Diese kann man im [Google Play Store](https://play.google.com/store/apps/details?id=com.sonymobile.hostapp.everest&hl=de) finden. 
Weiterhin muss das Smartphone rooted sein und die [aSQLiteManager](https://play.google.com/store/apps/details?id=dk.andsen.asqlitemanager&hl=en) App installiert sein. Dann können Datenbankdumps im CSV-Format exportiert werden, die dann vom SonySWR12Importer importiert werden können.

Konfiguration
-----
Das SonySWR12Importer-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem SonySWR12Importer kompatibel sind. | x
| delimiter | ; | Das Trennzeichen, das beim Lesen der CSV-Datei verwendet werden soll. | 

Benötigte Plugins
-----
Der SonySWR12Importer benötigt keine anderen Plugins.