package de.opendiabetes.vault.plugin.importer.googlecrawler.models;

public class Activity {
    private long startTime;
    private long endTime;
    private int activity;
    private int intensity;
    private int[] heartRate;

    private String location = "";

    public Activity(long startTime, long endTime, int activity) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.intensity = -1;
        this.heartRate = new int[3];
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getActivity() {
        return activity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int[] getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int[] heartRate) {
        this.heartRate = heartRate;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getID() {
        return endTime;
    }
}
