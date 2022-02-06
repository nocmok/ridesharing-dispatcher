package com.nocmok.orp.core_api;

import java.util.ArrayList;
import java.util.List;

public class RoadRoute {

    private List<RoadNode> route;

    private double cost;

    public RoadRoute(List<RoadNode> route, double cost) {
        this.route = new ArrayList<>(route);
        this.cost = cost;
    }

    public List<RoadNode> getRoute() {
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
