package com.nocmok.orp.state_keeper.api;

import com.nocmok.orp.solver.api.Schedule;

public interface VehicleState {

    String getId();

    VehicleStatus getStatus();

    Schedule getSchedule();

    Integer getCapacity();

    Integer getResidualCapacity();

    void setId(String id);

    void setStatus(VehicleStatus status);

    void setSchedule(Schedule schedule);

    void setCapacity(Integer capacity);

    void setResidualCapacity(Integer residualCapacity);
}
