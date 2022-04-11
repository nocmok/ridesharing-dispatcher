package com.nocmok.orp.road_index.mem_stub.solver;

import java.util.Collection;
import java.util.Map;

public interface Graph {

    Map<String, Link> getOutboundLinksMap(String nodeId);

    boolean containsNode(String nodeId);

    int nNodes();

    Node getNodeMetadata(String nodeId);

    Collection<Node> getAllNodes();

    public static class Node {
        private final String id;
        private final Double latitude;
        private final Double longitude;

        public Node(String id, Double latitude, Double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getId() {
            return id;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        @Override public String toString() {
            return "Node{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    public static class Link {
        private final String id;
        private final String startNodeId;
        private final String endNodeId;
        private final Double cost;

        public Link(String id, String startNodeId, String endNodeId, Double cost) {
            this.id = id;
            this.startNodeId = startNodeId;
            this.endNodeId = endNodeId;
            this.cost = cost;
        }

        public String getId() {
            return id;
        }

        public String getStartNodeId() {
            return startNodeId;
        }

        public String getEndNodeId() {
            return endNodeId;
        }

        public Double getCost() {
            return cost;
        }

        @Override public String toString() {
            return "Link{" +
                    "id='" + id + '\'' +
                    ", startNodeId='" + startNodeId + '\'' +
                    ", endNodeId='" + endNodeId + '\'' +
                    ", cost=" + cost +
                    '}';
        }
    }
}
