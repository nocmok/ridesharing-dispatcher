package com.nocmok.orp.core_api;

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

    @Override public String toString() {
        return "Road{" +
                "startNode=" + startNode +
                ", endNode=" + endNode +
                ", cost=" + cost +
                '}';
    }
}
