package com.nocmok.orp.postgres.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class OrderAssignment {

    private Long orderId;
    private Long sessionId;
    private Instant assignedAt;
}
