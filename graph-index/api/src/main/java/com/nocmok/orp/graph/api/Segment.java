package com.nocmok.orp.graph.api;

import java.util.Objects;

/**
 * Прямой ориентированный участок между двумя вершинами на графе
 */
public class Segment {
    private String id;
    private Node startNode;
    private Node endNode;
    private Double cost;

    public Segment(String id, Node startNode, Node endNode, Double cost) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public Double getCost() {
        return cost;
    }

    @Override public String toString() {
        return "Segment{" +
                "id='" + id + '\'' +
                ", startNode=" + startNode +
                ", endNode=" + endNode +
                ", cost=" + cost +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Segment segment = (Segment) o;
        return startNode.equals(segment.startNode) && endNode.equals(segment.endNode);
    }

    @Override public int hashCode() {
        return Objects.hash(startNode, endNode);
    }
}
