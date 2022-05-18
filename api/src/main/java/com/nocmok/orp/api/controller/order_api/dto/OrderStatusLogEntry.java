package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class OrderStatusLogEntry {

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("updatedAt")
    private Instant updatedAt;
}
