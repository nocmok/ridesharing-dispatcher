package com.nocmok.orp.api.controller.driver_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionResponse {

    @Schema(description = "Идентификатор созданной сессии",
            example = "1")
    private String sessionId;

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
