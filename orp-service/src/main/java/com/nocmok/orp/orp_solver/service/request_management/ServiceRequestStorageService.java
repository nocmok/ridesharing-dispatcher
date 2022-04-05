package com.nocmok.orp.orp_solver.service.request_management;

import com.nocmok.orp.core_api.Request;
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

        private Integer pickupNodeId;

        /**
         * Фактические координаты указанные клиентом
         */
        private Double pickupLat;

        /**
         * Фактические координаты указанные клиентом
         */
        private Double pickupLon;

        private Integer dropoffNodeId;

        /**
         * Фактические координаты указанные клиентом
         */
        private Double dropoffLat;

        /**
         * Фактические координаты указанные клиентом
         */
        private Double dropoffLon;

        private Instant requestedAt;

        private Double detourConstraint;

        private Integer maxPickupDelaySeconds;

        private Integer load;

        private Integer topK;
    }
}
