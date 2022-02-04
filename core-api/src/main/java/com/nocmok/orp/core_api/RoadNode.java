package com.nocmok.orp.core_api;

public class RoadNode {

    private int nodeId;

    private GCS coordinates;

    public RoadNode(int nodeId, GCS coordinates) {
        this.nodeId = nodeId;
        this.coordinates = coordinates;
    }

    public int getNodeId() {
        return nodeId;
    }

    public GCS getCoordinates() {
        return coordinates;
    }
}
