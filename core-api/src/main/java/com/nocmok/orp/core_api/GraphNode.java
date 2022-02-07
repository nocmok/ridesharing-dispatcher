package com.nocmok.orp.core_api;

public class GraphNode {

    private int nodeId;

    private GCS coordinates;

    public GraphNode(int nodeId, GCS coordinates) {
        this.nodeId = nodeId;
        this.coordinates = coordinates;
    }

    public int getNodeId() {
        return nodeId;
    }

    public GCS getCoordinates() {
        return coordinates;
    }

    @Override public String toString() {
        return "RoadNode{" +
                "nodeId=" + nodeId +
                ", coordinates=" + coordinates +
                '}';
    }
}
