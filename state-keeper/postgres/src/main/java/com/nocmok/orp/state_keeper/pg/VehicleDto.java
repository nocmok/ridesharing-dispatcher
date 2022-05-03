package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto implements VehicleState {

    private String id;
    private VehicleStatus status;
    private Schedule schedule;
    private Integer capacity;
    private Integer residualCapacity;

}
