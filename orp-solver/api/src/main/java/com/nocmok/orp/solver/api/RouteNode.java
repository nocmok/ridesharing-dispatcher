package com.nocmok.orp.solver.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RouteNode {

    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    public RouteNode() {

    }

    public RouteNode(String nodeId, Double latitude, Double longitude) {
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNodeId() {
        return nodeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteNode routeNode = (RouteNode) o;
        return nodeId.equals(routeNode.nodeId);
    }

    @Override public int hashCode() {
        return Objects.hash(nodeId);
    }

    @Override public String toString() {
        return "RouteNode{" +
                "nodeId='" + nodeId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
