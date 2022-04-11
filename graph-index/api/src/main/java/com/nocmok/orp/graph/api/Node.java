package com.nocmok.orp.graph.api;

import java.util.Objects;

/**
 * Вершина на графе
 */
public class Node {
    private String id;
    private Double latitude;
    private Double longitude;

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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }
}
