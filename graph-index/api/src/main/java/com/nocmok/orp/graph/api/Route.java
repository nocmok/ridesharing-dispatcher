package com.nocmok.orp.graph.api;

import java.util.List;

public class Route {

    private List<Segment> route;

    private Double routeCost;

    public Route(List<Segment> route, Double routeCost) {
        this.route = route;
        this.routeCost = routeCost;
    }

    public List<Segment> getRoute() {
        return route;
    }

    public Double getRouteCost() {
        return routeCost;
    }

    public boolean isEmpty() {
        return route.isEmpty();
    }

    public int size() {
        return route.size();
    }

    @Override public String toString() {
        return "Route{" +
                "route=" + route +
                ", routeCost=" + routeCost +
                '}';
    }
}
