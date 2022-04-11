package com.nocmok.orp.state_keeper.api;

import java.util.List;

public interface VehicleState {

//    protected String id;
//    protected VehicleStatus status;
//    protected List<ScheduleEntry> schedule;
//    protected Integer capacity;
//    protected Integer residualCapacity;

//    public VehicleState() {
//    }
//
//    public VehicleState(String id, VehicleStatus status, List<ScheduleEntry> schedule, Integer capacity, Integer residualCapacity) {
//        this.id = id;
//        this.status = status;
//        this.schedule = schedule;
//        this.capacity = capacity;
//        this.residualCapacity = residualCapacity;
//    }

    String getId();

    VehicleStatus getStatus();

    List<ScheduleEntry> getSchedule();

    Integer getCapacity();

    Integer getResidualCapacity();

    void setId(String id);

    void setStatus(VehicleStatus status);

    void setSchedule(List<ScheduleEntry> schedule);

    void setCapacity(Integer capacity);

    void setResidualCapacity(Integer residualCapacity);
}
