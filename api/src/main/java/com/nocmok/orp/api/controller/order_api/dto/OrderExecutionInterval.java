package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class OrderExecutionInterval {

    @JsonProperty("startTime")
    private Instant startTime;
    @JsonProperty("endTime")
    private Instant endTime;
    @Schema(description = "Дистанция пройденная тс за интервал времени в метрах")
    @JsonProperty("distance")
    private Double distance;
    @Schema(description = "Количество пассажиров находящихся в тс (заказов в состоянии SERVING).")
    @JsonProperty("companions")
    private Integer companions;
    @Schema(description = "Количество заказов выполняемых тс (заказов в состоянии ACCEPTED, PICKUP_PENDING, SERVING).")
    @JsonProperty("companionOrders")
    private Integer companionOrders;
    @Schema(description = "Состояние заказа.")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Длительность интервала в секундах.")
    @JsonGetter("duration")
    private Long durationSeconds() {
        if (endTime == null || startTime == null) {
            return null;
        }
        return endTime.getEpochSecond() - startTime.getEpochSecond();
    }
}
