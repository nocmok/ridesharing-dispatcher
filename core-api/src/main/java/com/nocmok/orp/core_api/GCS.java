package com.nocmok.orp.core_api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCS {
    /**
     * Latitude
     */
    @JsonProperty("latitude")
    private Double lat;

    /**
     * Longitude
     */
    @JsonProperty("longitude")
    private Double lon;

    public GCS() {

    }

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
