# SourceCodeExporter
Version: 1.0.0  
Klassifikation: Exporter

Übersicht
-----
Der SourceCodeExporter ist ein Exporter-Plugin, das Daten aus der Datenbank als Java-Quellcode exportiert.

Beispieldatensatz
-----

```
public static List<VaultEntry> getStaticDataset() throws ParseException {
    List<VaultEntry> vaultEntries = new ArrayList<>();
    List<VaultEntryAnnotation> tmpAnnotations;
    tmpAnnotations = new ArrayList<>();
    tmpAnnotations.add(new VaultEntryAnnotation(VaultEntryAnnotation.TYPE.CGM_VENDOR_MEDTRONIC"));
    vaultEntries.add(new VaultEntry(VaultEntryType.GLUCOSE_CGM_CALIBRATION,TimestampUtils.createCleanTimestamp("2010.03.01-01:00","yyyy.MM.dd-HH:mm"),243.0,tmpAnnotations));
}
```

Konfiguration
-----
Das SourceCodeExporter-Plugin bietet folgende Konfigurationsmöglichkeiten:

| Schlüssel  | Wert | Beschreibung | notwendig |
| ------------- | ------------- |  ------------- | ------------- |
| compatiblePlugins | PluginA, PluginB, PluginC | Eine Liste von Plugins, die mit dem SourceCodeExporter kompatibel sind. | x
| periodRestriction | true | Gibt an, ob nur Daten aus einem beschränkten Zeitraum exportiert werden sollen. (Standardwert: false) | 
| periodRestrictionFrom | 24/04/2015 | Start des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |
| periodRestrictionTo | 24/05/2015 | Ende des Export Zeitraums. Notwendig, wenn periodRestriction true ist. |

Benötigte Plugins
-----
Der SourceCodeExporter benötigt keine anderen Plugins.