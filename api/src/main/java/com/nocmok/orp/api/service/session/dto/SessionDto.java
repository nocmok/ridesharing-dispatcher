package com.nocmok.orp.api.service.session.dto;

import com.nocmok.orp.solver.api.ScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SessionDto {

    private String sessionId;
    private Long capacity;
    private Long residualCapacity;
    private List<ScheduleEntry> schedule;
}
