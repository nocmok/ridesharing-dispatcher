package com.nocmok.orp.core_api;

public class GCS {
    /**
     * Latitude
     */
    private final Double lat;

    /**
     * Longitude
     */
    private final Double lon;

    public GCS(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double lat() {
        return lat;
    }

    public Double lon() {
        return lon;
    }

    @Override public String toString() {
        return "GCS{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
