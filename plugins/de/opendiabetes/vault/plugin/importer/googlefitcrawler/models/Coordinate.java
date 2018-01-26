package de.opendiabetes.vault.plugin.importer.googlefitcrawler.models;

public class Coordinate {
    private long timestamp;

    private double longitude;
    private double latitude;
    private int altitude;
    private int accuracy; // in meter

    public static final int NO_ALTITUDE_AVAILABLE = -1;


    public Coordinate(long timestamp, double longitude, double latitude, int accuracy, int altitude) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
    }

    public Coordinate(long timestamp, double longitude, double latitude, int accuracy) {
        this(timestamp, longitude, latitude, accuracy, NO_ALTITUDE_AVAILABLE);
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public long getTimestamp(){
        return timestamp;
    }

}
