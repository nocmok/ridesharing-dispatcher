package com.nocmok.orp.api.controller.driver_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Schema(description = "Начальные параметры для создания новой сессии")
public class CreateSessionRequest {

    @Schema(description = "Вместимость транспортного средства. " +
            "Измеряется в количестве человек, которых одновременно может перевозить транспортное средство.",
            example = "2")
    @JsonProperty("capacity")
    private Integer capacity;

    @Schema(description = "Текущие координаты транспортного средства.")
    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @Schema(description = "Дорога на которой находится транспортное средство")
    @JsonProperty("road")
    private RoadSegment road;

    @Schema(description = "Время создания сессии",
            example = "2022-04-15T13:11:21.639114Z")
    @JsonProperty("createdAt")
    private Instant createdAt;
}
