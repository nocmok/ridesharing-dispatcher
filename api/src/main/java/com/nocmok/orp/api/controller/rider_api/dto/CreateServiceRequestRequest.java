package com.nocmok.orp.api.controller.rider_api.dto;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateServiceRequestRequest {

    @Schema(description = "Указанные координаты точки посадки")
    private Coordinates recordedOrigin;
    @Schema(description = "Указанные координаты точки высадки")
    private Coordinates recordedDestination;
    @Schema(description = "Идентификатор дорожного сегмента с которого осуществляется посадка.")
    private RoadSegment pickupRoadSegment;
    @Schema(description = "Идентификатор дорожного сегмента с которого осуществляется высадка.")
    private RoadSegment dropoffRoadSegment;
    @Schema(description = "Во сколько раз можно увеличить время в поездке для применения райдшеринга.", example = "1.3", minimum = "1")
    private Double detourConstraint;
    @Schema(description = "Максимальное время ожидания машины. Отсчитывается от значения в поле requestedAt", example = "300")
    private Integer maxPickupDelaySeconds;
    private Instant requestedAt;
    @Schema(description = "Количество пассажиров", minimum = "1")
    private Integer load;

}
