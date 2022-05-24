package com.nocmok.orp.api.controller.driver_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import com.nocmok.orp.api.controller.common_dto.SessionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionResponse {

    @Schema(description = "созданная сессия")
    @JsonProperty("createdSession")
    private SessionDto createdSession;

    @Schema(description = "Текущие координаты транспортного средства.")
    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @Schema(description = "Дорога на которой находится транспортное средство")
    @JsonProperty("road")
    private RoadSegment road;
}
