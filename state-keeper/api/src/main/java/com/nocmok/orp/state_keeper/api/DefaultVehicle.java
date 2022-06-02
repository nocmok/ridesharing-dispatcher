package com.nocmok.orp.state_keeper.api;

import com.nocmok.orp.solver.api.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultVehicle implements VehicleState {

    private String id;
    private VehicleStatus status;
    private Schedule schedule;
    private Integer capacity;
    private Integer residualCapacity;
}
