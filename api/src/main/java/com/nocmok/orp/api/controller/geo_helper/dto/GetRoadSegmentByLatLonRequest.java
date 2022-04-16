package com.nocmok.orp.api.controller.geo_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на получение дорожного сегмента по координатам.")
public class GetRoadSegmentByLatLonRequest {

    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @JsonProperty("rightHandTraffic")
    private boolean rightHandTraffic;
}
