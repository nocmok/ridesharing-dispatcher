package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegmentWithGeodata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionGeodata {

    @JsonProperty("sessionId")
    private String sessionId;

    @Schema(nullable = true)
    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @Schema(nullable = true)
    @JsonProperty("road")
    private RoadSegmentWithGeodata roadSegment;
}
