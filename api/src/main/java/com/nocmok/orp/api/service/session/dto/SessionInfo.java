package com.nocmok.orp.api.service.session.dto;

import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SessionInfo {

    private String id;
    private SessionStatus status;
    private List<ScheduleEntry> schedule;
    private List<RouteNode> routeScheduled;
    private Long capacity;
    private Long residualCapacity;
    private Instant createdAt;
    private Instant completedAt;
}
