package de.opendiabetes.vault.plugin.importer.googlecrawler.models;

import java.util.ArrayList;
import java.util.List;

public class ResolvedLocations {
    private static ResolvedLocations instance;

    private List<Location> locations;

    private ResolvedLocations() {
        locations = new ArrayList<>();
    }

    public static ResolvedLocations getInstance() {
        if (ResolvedLocations.instance == null) {
            ResolvedLocations.instance = new ResolvedLocations();
        }
        return ResolvedLocations.instance;
    }

    public void addLocation(Location loc){
        locations.add(loc);
    }

    public void importFromJson(ResolvedLocations resLoc){
        for(Location loc : resLoc.locations){
            locations.add(loc);
        }
    }

    public List<Location> getLocations() {
        return locations;
    }
}
