package com.nocmok.orp.proto.solver;

import lombok.Getter;

import java.util.List;

@Getter
public class Route {

    private List<Integer> route;
    private double distance;

    public Route(List<Integer> route, double distance) {
        this.route = route;
        this.distance = distance;
    }
}
