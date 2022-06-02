package com.nocmok.orp.api.controller.common_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Координаты в формате WGS84")
public class Coordinates {

    @Schema(example = "55.66971000000001")
    private Double latitude;

    @Schema(example = "37.28309499999999")
    private Double longitude;
}
