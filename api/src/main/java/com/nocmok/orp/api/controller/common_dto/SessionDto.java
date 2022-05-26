package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SessionDto {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("schedule")
    private List<ScheduleNode> schedule;

    @JsonProperty("capacity")
    private Long capacity;

    @JsonProperty("residualCapacity")
    private Long residualCapacity;

    @JsonProperty("startedAt")
    private Instant startedAt;

    @JsonProperty("terminatedAt")
    private Instant terminatedAt;
}
