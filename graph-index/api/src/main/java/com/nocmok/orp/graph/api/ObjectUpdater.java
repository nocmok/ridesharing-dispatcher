package com.nocmok.orp.graph.api;

public class ObjectUpdater {
    private String id;
    private String segmentStartNodeId;
    private String segmentEndNodeId;
    private Double latitude;
    private Double longitude;

    public ObjectUpdater(SpatialGraphObject graphObject) {
        this.id = graphObject.getId();
        this.segmentStartNodeId = graphObject.getSegment().getStartNode().getId();
        this.segmentEndNodeId = graphObject.getSegment().getEndNode().getId();
        this.latitude = graphObject.getLatitude();
        this.longitude = graphObject.getLongitude();
    }

    public ObjectUpdater(String id, String segmentStartNodeId, String segmentEndNodeId, Double latitude, Double longitude) {
        this.id = id;
        this.segmentStartNodeId = segmentStartNodeId;
        this.segmentEndNodeId = segmentEndNodeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getSegmentStartNodeId() {
        return segmentStartNodeId;
    }

    public String getSegmentEndNodeId() {
        return segmentEndNodeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setSegmentStartNodeId(String segmentStartNodeId) {
        this.segmentStartNodeId = segmentStartNodeId;
    }

    public void setSegmentEndNodeId(String segmentEndNodeId) {
        this.segmentEndNodeId = segmentEndNodeId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override public String toString() {
        return "ObjectUpdater{" +
                "id='" + id + '\'' +
                ", segmentStartNodeId='" + segmentStartNodeId + '\'' +
                ", segmentEndNodeId='" + segmentEndNodeId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
