package com.nocmok.orp.core_api;

import java.util.List;
import java.util.Optional;

public interface Vehicle {
    String getId();
    VehicleStatus getStatus();
    GCS getGCS();
    List<ScheduleNode> getSchedule();
    Optional<ScheduleNode> getNextScheduleNode();
    int getCapacity();
    int getResidualCapacity();
    void setResidualCapacity(int capacity);
}
