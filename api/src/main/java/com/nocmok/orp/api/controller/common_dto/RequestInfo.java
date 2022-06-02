package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("recordedOrigin")
    private Coordinates recordedOrigin;

    @JsonProperty("recordedDestination")
    private Coordinates recordedDestination;

    @JsonProperty("pickupRoadSegment")
    private RoadSegment pickupRoadSegment;

    @JsonProperty("dropoffRoadSegment")
    private RoadSegment dropoffRoadSegment;

    @JsonProperty("detourConstraint")
    private Double detourConstraint;

    @JsonProperty("maxPickupDelaySeconds")
    private Integer maxPickupDelaySeconds;

    @JsonProperty("requestedAt")
    private Instant requestedAt;

    @JsonProperty("completedAt")
    private Instant completedAt;

    @JsonProperty("load")
    private Integer load;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("servingSessionId")
    private String servingSessionId;
}
