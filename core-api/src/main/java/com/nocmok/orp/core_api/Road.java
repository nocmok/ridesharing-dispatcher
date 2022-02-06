package com.nocmok.orp.core_api;

public class Road {

    /**
     * Начальная точка ребра
     */
    private final RoadNode startNode;

    /**
     * Конечная точка ребра
     */
    private final RoadNode endNode;

    /**
     * Стоимость прохождения по ребру
     */
    private final double cost;

    public Road(RoadNode startNode, RoadNode endNode, double cost) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.cost = cost;
    }

    public RoadNode getStartNode() {
        return startNode;
    }

    public RoadNode getEndNode() {
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
