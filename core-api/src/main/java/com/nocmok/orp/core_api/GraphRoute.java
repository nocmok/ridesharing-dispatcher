package com.nocmok.orp.core_api;

import java.util.ArrayList;
import java.util.List;

public class GraphRoute {

    private List<GraphNode> route;

    private double cost;

    public GraphRoute(List<GraphNode> route, double cost) {
        this.route = new ArrayList<>(route);
        this.cost = cost;
    }

    public List<GraphNode> getRoute() {
        return route;
    }

    public double getCost() {
        return cost;
    }

    @Override public String toString() {
        return "RoadRoute{" +
                "route=" + route +
                ", cost=" + cost +
                '}';
    }
}
