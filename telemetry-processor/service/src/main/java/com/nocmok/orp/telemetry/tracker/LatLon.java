package com.nocmok.orp.telemetry.tracker;

public class LatLon {
    private final Double latitude;
    private final Double longitude;

    public LatLon(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override public String toString() {
        return "LatLon{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
