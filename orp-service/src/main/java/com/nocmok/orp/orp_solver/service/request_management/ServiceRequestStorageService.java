package com.nocmok.orp.orp_solver.service.request_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Optional;

public interface ServiceRequestStorageService {

    Optional<ServiceRequestDto> getRequestById(String id);

    void storeRequest(ServiceRequestDto request);

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ServiceRequestDto {

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
    }
}
