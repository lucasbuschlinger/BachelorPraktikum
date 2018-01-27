package de.opendiabetes.vault.plugin.importer.googlecrawler.models;

public class Datapoint {
    private long timestamp;
    private Coordinate coordinate;
    private String activity;

    public Datapoint() {
    }

    public Datapoint(Coordinate coord, long timestamp) {
        this.timestamp = timestamp;
        this.coordinate = coord;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
