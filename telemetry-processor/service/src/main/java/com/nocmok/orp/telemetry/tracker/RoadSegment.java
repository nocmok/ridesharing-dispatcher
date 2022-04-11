package com.nocmok.orp.telemetry.tracker;

import java.util.Objects;

public class RoadSegment {

    private final String startNodeId;
    private final String endNodeId;

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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoadSegment that = (RoadSegment) o;
        return startNodeId.equals(that.startNodeId) && endNodeId.equals(that.endNodeId);
    }

    @Override public int hashCode() {
        return Objects.hash(startNodeId, endNodeId);
    }
}
