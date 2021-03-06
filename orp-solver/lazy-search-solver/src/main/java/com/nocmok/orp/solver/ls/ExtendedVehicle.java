package com.nocmok.orp.solver.ls;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;

/**
 * Обертка над интерфейсом тс, обогащенная геоданными
 */
class ExtendedVehicle {

    private final String id;
    private final VehicleStatus status;
    private final Schedule schedule;
    private final NodesRoute routeScheduled;
    private final Integer capacity;
    private final Integer residualCapacity;
    private final Double latitude;
    private final Double longitude;
    private final Segment roadSegment;
    private final Double progressOnRoadSegment;

    public ExtendedVehicle(VehicleState state, Double latitude, Double longitude, Segment roadSegment, Double progressOnRoadSegment,
                           NodesRoute routeScheduled) {
        this.id = state.getId();
        this.status = state.getStatus();
        this.schedule = state.getSchedule();

        this.routeScheduled = routeScheduled;
        this.capacity = state.getCapacity();
        this.residualCapacity = state.getResidualCapacity();
        this.latitude = latitude;
        this.longitude = longitude;
        this.roadSegment = roadSegment;
        this.progressOnRoadSegment = progressOnRoadSegment;
    }

    public String getId() {
        return id;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public NodesRoute getRouteScheduled() {
        return routeScheduled;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getResidualCapacity() {
        return residualCapacity;
    }

    public Double getCostToNextNodeInScheduledRoute() {
        return roadSegment.getCost() * (1 - progressOnRoadSegment);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Segment getRoadSegment() {
        return roadSegment;
    }

    /**
     * Число в диапазоне от 0 до 1, где
     * 0 соответствует положению транспортного средства в начальной точке дороги,
     * 1 соответствует положению транспортного средства в конечной точке дороги.
     * Промежуточные значения примерно отражают степень прохождения дороги транспортным средством,
     * например 0.5 означает, что транспортное средство преодолело половину дорожного участка
     */
    public Double getProgressOnRoadSegment() {
        return progressOnRoadSegment;
    }
}
