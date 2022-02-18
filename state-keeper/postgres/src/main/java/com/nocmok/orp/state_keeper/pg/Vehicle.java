package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public class Vehicle implements com.nocmok.orp.core_api.Vehicle {

    private String id;
    private VehicleStatus status;
    private List<ScheduleNode> schedule;
    private List<GraphNode> routeScheduled;
    private int capacity;
    private int residualCapacity;
    private GraphBinding roadBinding;

    public Vehicle() {
    }

    @Override public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    @Override public VehicleStatus getStatus() {
        return status;
    }

    @Override public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    @Override public List<ScheduleNode> getSchedule() {
        return schedule;
    }

    @Override public void setSchedule(List<ScheduleNode> schedule) {
        this.schedule = schedule;
    }

    @Override public Integer getCapacity() {
        return capacity;
    }

    void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override public Integer getResidualCapacity() {
        return residualCapacity;
    }

    @Override public void setResidualCapacity(int residualCapacity) {
        this.residualCapacity = residualCapacity;
    }

    @Override public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", schedule=" + schedule +
                ", routeScheduled=" + routeScheduled +
                ", capacity=" + capacity +
                ", residualCapacity=" + residualCapacity +
                ", roadBinding=" + roadBinding +
                '}';
    }

    @Override public GraphBinding getRoadBinding() {
        return roadBinding;
    }

    public void setRoadBinding(GraphBinding roadBinding) {
        this.roadBinding = roadBinding;
    }

    @Override public List<GraphNode> getRouteScheduled() {
        return routeScheduled;
    }

    @Override public void setRouteScheduled(List<GraphNode> routeScheduled) {
        this.routeScheduled = routeScheduled;
    }
}
