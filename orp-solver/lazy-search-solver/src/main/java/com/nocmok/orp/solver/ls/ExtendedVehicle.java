package com.nocmok.orp.solver.ls;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStatus;

import java.util.List;

/**
 * Обертка над интерфейсом тс, обогащенная геоданными
 */
class ExtendedVehicle {

    private Vehicle underlyingVehicle;
    private Double costToNextNodeInScheduledRoute;
    private GCS gcs;

    public ExtendedVehicle(Vehicle vehicle) {
        this.underlyingVehicle = vehicle;
    }

    public Vehicle getUnderlyingVehicle() {
        return underlyingVehicle;
    }

    public String getId() {
        return underlyingVehicle.getId();
    }

    public VehicleStatus getStatus() {
        return underlyingVehicle.getStatus();
    }

    public void setStatus(VehicleStatus status) {
        underlyingVehicle.setStatus(status);
    }

    public List<ScheduleNode> getSchedule() {
        return underlyingVehicle.getSchedule();
    }

    public void setSchedule(List<ScheduleNode> schedule) {
        underlyingVehicle.setSchedule(schedule);
    }

    public Integer getCapacity() {
        return underlyingVehicle.getCapacity();
    }

    public Integer getResidualCapacity() {
        return underlyingVehicle.getResidualCapacity();
    }

    public void setResidualCapacity(int capacity) {
        underlyingVehicle.setResidualCapacity(capacity);
    }

    public GraphBinding getRoadBinding() {
        return underlyingVehicle.getRoadBinding();
    }

    public List<GraphNode> getRouteScheduled() {
        return underlyingVehicle.getRouteScheduled();
    }

    /**
     * Возвращает стоимость перемещения до следующей в маршруте вершины
     */
    public Double getCostToNextNodeInScheduledRoute() {
        return this.costToNextNodeInScheduledRoute;
    }

    public void setCostToNextNodeInScheduledRoute(Double costToNextNodeInScheduledRoute) {
        this.costToNextNodeInScheduledRoute = costToNextNodeInScheduledRoute;
    }

    /**
     * Возвращает текущую координату тс
     */
    public GCS getCurrentGCS() {
        return this.gcs;
    }
}
