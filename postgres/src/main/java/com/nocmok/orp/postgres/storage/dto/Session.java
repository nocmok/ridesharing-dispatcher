package com.nocmok.orp.postgres.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class Session {
    private Long sessionId;
    private String scheduleJson;
    private Long totalCapacity;
    private Long residualCapacity;

    @Data
    @AllArgsConstructor
    public static class StatusLogEntry {
        private SessionStatus status;
        private Instant timestamp;
    }
}
