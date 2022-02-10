package com.nocmok.orp.core_api;

import java.util.List;

public class RequestMatching {

    private final Request request;
    private final Vehicle servingVehicle;
    private final Double cost;
    private final List<GraphNode> servingRoute;
    private final List<ScheduleNode> servingPlan;

    public RequestMatching(Request request, Vehicle servingVehicle, Double cost, List<GraphNode> servingRoute,
                           List<ScheduleNode> servingPlan) {
        this.request = request;
        this.servingVehicle = servingVehicle;
        this.cost = cost;
        this.servingRoute = servingRoute;
        this.servingPlan = servingPlan;
    }

    public Request getRequest() {
        return request;
    }

    public Vehicle getServingVehicle() {
        return servingVehicle;
    }

    public Double getCost() {
        return cost;
    }

    public List<GraphNode> getServingRoute() {
        return servingRoute;
    }

    public List<ScheduleNode> getServingPlan() {
        return servingPlan;
    }

    @Override public String toString() {
        return "RequestMatching{" +
                "request=" + request +
                ", servingVehicle=" + servingVehicle +
                ", cost=" + cost +
                ", servingRoute=" + servingRoute +
                ", servingPlan=" + servingPlan +
                '}';
    }
}
