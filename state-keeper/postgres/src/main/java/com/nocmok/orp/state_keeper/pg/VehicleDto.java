package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto implements VehicleState {

    private String id;
    private VehicleStatus status;
    private List<ScheduleEntry> schedule;
    private Integer capacity;
    private Integer residualCapacity;

}
