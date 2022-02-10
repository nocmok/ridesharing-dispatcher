package com.nocmok.orp.core_api;

import java.util.Objects;

public class GraphNode {

    private final int nodeId;

    private final GCS coordinates;

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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphNode graphNode = (GraphNode) o;
        return nodeId == graphNode.nodeId;
    }

    @Override public int hashCode() {
        return Objects.hash(nodeId);
    }

    @Override public String toString() {
        return "RoadNode{" +
                "nodeId=" + nodeId +
                ", coordinates=" + coordinates +
                '}';
    }
}
