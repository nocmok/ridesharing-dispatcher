package com.nocmok.orp.api.controller.driver_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Schema(description = "Для указания сегмента дороги")
public class RoadSegment {

    @Schema(description = "Идентификатор начальной точки сегмента.", example = "2029641451")
    private String sourceId;
    @Schema(description = "Идентификатор конечной точки сегмента.", example = "1122380405")
    private String targetId;
}
