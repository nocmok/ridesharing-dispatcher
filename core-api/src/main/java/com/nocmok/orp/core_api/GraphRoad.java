package com.nocmok.orp.core_api;

import java.util.Objects;

public class GraphRoad {

    /**
     * Начальная точка ребра
     */
    private final GraphNode startNode;

    /**
     * Конечная точка ребра
     */
    private final GraphNode endNode;

    /**
     * Стоимость прохождения по ребру
     */
    private final double cost;

    public GraphRoad(GraphNode startNode, GraphNode endNode, double cost) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.cost = cost;
    }

    public GraphNode getStartNode() {
        return startNode;
    }

    public GraphNode getEndNode() {
        return endNode;
    }

    public double getCost() {
        return cost;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphRoad graphRoad = (GraphRoad) o;
        return startNode.equals(graphRoad.startNode) && endNode.equals(graphRoad.endNode);
    }

    @Override public int hashCode() {
        return Objects.hash(startNode, endNode);
    }

    @Override public String toString() {
        return "Road{" +
                "startNode=" + startNode +
                ", endNode=" + endNode +
                ", cost=" + cost +
                '}';
    }
}
