package com.nocmok.orp.postgres.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ServiceRequest {
    private String requestId;

    private Double recordedOriginLatitude;

    private Double recordedOriginLongitude;

    private Double recordedDestinationLatitude;

    private Double recordedDestinationLongitude;

    private String pickupRoadSegmentStartNodeId;

    private String pickupRoadSegmentEndNodeId;

    private String dropOffRoadSegmentStartNodeId;

    private String dropOffRoadSegmentEndNodeId;

    private Instant requestedAt;

    private Double detourConstraint;

    private Integer maxPickupDelaySeconds;

    private Integer load;

    private OrderStatus status;

    private String servingSessionId;
}