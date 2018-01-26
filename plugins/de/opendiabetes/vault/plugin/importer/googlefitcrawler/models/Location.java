package de.opendiabetes.vault.plugin.importer.googlefitcrawler.models;

import com.google.maps.model.LatLng;

public class Location {
    public LatLng coordinate;
    public String name;

    public Location(LatLng coord, String name) {
        this.coordinate = coord;
        this.name = name;
    }

    public Location() {
    }
}
