package com.nocmok.orp.core_api;

public class GCS {
    /**
     * Latitude
     */
    private final double lat;

    /**
     * Longitude
     */
    private final double lon;

    public GCS(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }
}
