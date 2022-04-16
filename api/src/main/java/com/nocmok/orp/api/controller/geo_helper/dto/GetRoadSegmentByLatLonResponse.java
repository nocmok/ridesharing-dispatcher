package com.nocmok.orp.api.controller.geo_helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegmentWithGeodata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRoadSegmentByLatLonResponse {

    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @JsonProperty("rightHandTraffic")
    private boolean rightHandTraffic;

    @JsonProperty("road")
    private RoadSegmentWithGeodata road;
}
