package com.nocmok.orp.api.storage.request_management.dto;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestInfo {

    private String requestId;
    private Coordinates recordedOrigin;
    private Coordinates recordedDestination;
    private RoadSegment pickupRoadSegment;
    private RoadSegment dropoffRoadSegment;
    private Double detourConstraint;
    private Integer maxPickupDelaySeconds;
    private Instant requestedAt;
    private Integer load;
    private OrderStatus status;
}
