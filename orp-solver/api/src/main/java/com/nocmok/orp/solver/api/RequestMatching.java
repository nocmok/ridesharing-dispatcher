package com.nocmok.orp.solver.api;

import java.util.List;

public class RequestMatching {

    private final String servingVehicleId;
    /**
     * План тс на момент, когда алгоритм пересчитывал план тс.
     * Может использоваться для того, чтобы проверять актуальность вычислений.
     */
    private final List<ScheduleNode> oldServingPlan;
    private final List<ScheduleNode> servingPlan;
    private final List<RouteNode> servingRoute;
    /**
     * Разница в стоимости построенного алгоритмом маршрута и текущего маршрута транспортного средства.
     */
    private final Double additionalCost;

    public RequestMatching(String servingVehicleId, List<ScheduleNode> oldServingPlan, List<ScheduleNode> servingPlan,
                           List<RouteNode> servingRoute, Double additionalCost) {
        this.servingVehicleId = servingVehicleId;
        this.oldServingPlan = oldServingPlan;
        this.servingPlan = servingPlan;
        this.servingRoute = servingRoute;
        this.additionalCost = additionalCost;
    }

    public List<ScheduleNode> getOldServingPlan() {
        return oldServingPlan;
    }

    public String getServingVehicleId() {
        return servingVehicleId;
    }

    public List<ScheduleNode> getServingPlan() {
        return servingPlan;
    }

    public List<RouteNode> getServingRoute() {
        return servingRoute;
    }

    public Double getAdditionalCost() {
        return additionalCost;
    }

    @Override public String toString() {
        return "RequestMatching{" +
                "servingVehicleId='" + servingVehicleId + '\'' +
                ", servingPlan=" + servingPlan +
                ", servingRoute=" + servingRoute +
                ", additionalCost=" + additionalCost +
                '}';
    }
}
