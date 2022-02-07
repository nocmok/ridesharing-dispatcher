package com.nocmok.orp.vss.pg;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
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
    private GCS gcs;
    private List<ScheduleNode> schedule;
    private int capacity;
    private int residualCapacity;
    private GraphBinding roadBinding;
    private double distanceScheduled;

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

    @Override public GCS getGCS() {
        return gcs;
    }

    @Override public List<ScheduleNode> getSchedule() {
        return schedule;
    }

    @Override public void setSchedule(List<ScheduleNode> schedule) {
        this.schedule = schedule;
    }

    @Override public int getCapacity() {
        return capacity;
    }

    void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override public int getResidualCapacity() {
        return residualCapacity;
    }

    @Override public void setResidualCapacity(int residualCapacity) {
        this.residualCapacity = residualCapacity;
    }

    void setGcs(GCS gcs) {
        this.gcs = gcs;
    }

    @Override public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", gcs=" + gcs +
                ", schedule=" + schedule +
                ", capacity=" + capacity +
                ", residualCapacity=" + residualCapacity +
                ", roadBinding=" + roadBinding +
                ", distanceScheduled=" + distanceScheduled +
                '}';
    }

    @Override public GraphBinding getRoadBinding() {
        return roadBinding;
    }

    @Override public double getDistanceScheduled() {
        return distanceScheduled;
    }
}
