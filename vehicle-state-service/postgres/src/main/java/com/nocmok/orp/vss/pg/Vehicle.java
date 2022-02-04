package com.nocmok.orp.vss.pg;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.VehicleStatus;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public class Vehicle implements com.nocmok.orp.core_api.Vehicle {

    private String id;
    private VehicleStatus status;
    private GCS gcs;
    private List<ScheduleNode> schedule;
    private int capacity;
    private int residualCapacity;

    @Override public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    @Override public VehicleStatus getStatus() {
        return status;
    }

    void setStatus(VehicleStatus status) {
        this.status = status;
    }

    @Override public GCS getGCS() {
        return gcs;
    }

    @Override public List<ScheduleNode> getSchedule() {
        return schedule;
    }

    void setSchedule(List<ScheduleNode> schedule) {
        this.schedule = schedule;
    }

    @Override public Optional<ScheduleNode> getNextScheduleNode() {
        return schedule.isEmpty() ? Optional.empty() : Optional.of(schedule.get(0));
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


}
