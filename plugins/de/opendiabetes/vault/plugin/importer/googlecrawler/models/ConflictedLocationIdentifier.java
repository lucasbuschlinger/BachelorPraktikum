package de.opendiabetes.vault.plugin.importer.googlecrawler.models;

import com.google.maps.model.LatLng;

public class ConflictedLocationIdentifier {
    public long timestamp;
    public LatLng coordinate;

    public ConflictedLocationIdentifier(long timestamp, LatLng coordinate){
        this.timestamp = timestamp;
        this.coordinate = coordinate;
    }
}
