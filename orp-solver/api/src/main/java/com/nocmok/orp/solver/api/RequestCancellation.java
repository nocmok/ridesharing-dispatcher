package com.nocmok.orp.solver.api;

import java.util.List;

public class RequestCancellation {

    private final Schedule updatedSchedule;
    private final List<RouteNode> updatedRoute;

    public RequestCancellation(Schedule updatedSchedule, List<RouteNode> updatedRoute) {
        this.updatedSchedule = updatedSchedule;
        this.updatedRoute = updatedRoute;
    }

    public Schedule getUpdatedSchedule() {
        return updatedSchedule;
    }

    public List<RouteNode> getUpdatedRoute() {
        return updatedRoute;
    }

    @Override public String toString() {
        return "RequestCancellation{" +
                "updatedSchedule=" + updatedSchedule +
                ", updatedRoute=" + updatedRoute +
                '}';
    }
}
