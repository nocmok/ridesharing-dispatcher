package com.nocmok.orp.simulator.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSession {

    private String sessionId;
    private Instant createdAt;
    private Instant completedAt;
    private String status;
    private Integer totalCapacity;
    private Integer residualCapacity;

    // TODO добавить план и маршрут
}
