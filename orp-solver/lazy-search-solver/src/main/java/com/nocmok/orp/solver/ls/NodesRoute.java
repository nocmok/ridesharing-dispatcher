package com.nocmok.orp.solver.ls;

import com.nocmok.orp.graph.api.Node;

import java.util.Collections;
import java.util.List;

public class NodesRoute {

    private static final NodesRoute emptyRoute = new NodesRoute(Collections.emptyList(), 0d);
    private List<Node> route;
    private Double routeCost;

    public NodesRoute(List<Node> route, Double routeCost) {
        this.route = route;
        this.routeCost = routeCost;
    }

    public static NodesRoute emptyRoute() {
        return emptyRoute;
    }

    public List<Node> getRoute() {
        return route;
    }

    public Double getRouteCost() {
        return routeCost;
    }

    @Override public String toString() {
        return "NodesRoute{" +
                "route=" + route +
                ", routeCost=" + routeCost +
                '}';
    }
}
