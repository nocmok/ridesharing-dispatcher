package com.nocmok.orp.graph.api;

/**
 * Объект привязанный к графу
 */
public class SpatialGraphObject {
    private String id;
    private Segment segment;
    private Double latitude;
    private Double longitude;

    public SpatialGraphObject(String id, Segment segment, Double latitude, Double longitude) {
        this.id = id;
        this.segment = segment;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public Segment getSegment() {
        return segment;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override public String toString() {
        return "GraphObject{" +
                "id='" + id + '\'' +
                ", segment=" + segment +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
