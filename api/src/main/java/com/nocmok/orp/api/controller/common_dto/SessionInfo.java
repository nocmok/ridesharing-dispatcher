package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sessionStatus")
    private VehicleStatus sessionStatus;

    @JsonProperty("schedule")
    private List<ScheduleNode> schedule;

    @JsonProperty("routeScheduled")
    @Schema(nullable = true)
    private List<Node> routeScheduled;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("residualCapacity")
    private Integer residualCapacity;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("completedAt")
    private Instant completedAt;
}
