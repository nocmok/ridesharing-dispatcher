package com.nocmok.orp.solver.api;

public class RoadSegment {

    private String startNodeId;
    private String endNodeId;

    public RoadSegment(String startNodeId, String endNodeId) {
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public String getEndNodeId() {
        return endNodeId;
    }

    @Override public String toString() {
        return "RoadSegment{" +
                "startNodeId='" + startNodeId + '\'' +
                ", endNodeId='" + endNodeId + '\'' +
                '}';
    }
}
