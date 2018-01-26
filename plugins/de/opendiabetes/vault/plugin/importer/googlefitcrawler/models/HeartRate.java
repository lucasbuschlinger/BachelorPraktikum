package de.opendiabetes.vault.plugin.importer.googlefitcrawler.models;

public class HeartRate {
    private long timestamp;
    private int rate;

    public HeartRate(long timestamp, int rate){
        this.timestamp = timestamp;
        this.rate = rate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRate() {
        return rate;
    }
}
