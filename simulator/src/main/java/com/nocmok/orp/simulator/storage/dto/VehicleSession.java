package com.nocmok.orp.simulator.storage.dto;

import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.solver.api.ScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSession {

    private String sessionId;
    private Instant createdAt;
    private Instant completedAt;
    private SessionStatus status;
    private Integer totalCapacity;
    private Integer residualCapacity;
    private List<ScheduleEntry> schedule;

}
