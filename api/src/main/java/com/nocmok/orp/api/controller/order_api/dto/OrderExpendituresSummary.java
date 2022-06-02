package com.nocmok.orp.api.controller.order_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderExpendituresSummary {

    @Schema(description = "Расстояние посчитанное на момент отправки заказа")
    private Double distanceScheduled;

    @Schema(description = "Фактическое пройденное расстояние")
    private Double distanceTravelled;

    @Schema(description = "Время ожидания диспетчеризации (время от отправки заказа до подтверждения/отклонения системой или водителем)")
    private Long dispatchWaitingTime;

    @Schema(description = "Время ожидания тс")
    private Long serviceWaitingTime;

    @Schema(description = "Время ожидания клиента")
    private Long pickupWaitingTime;

    @Schema(description = "Время в пути")
    private Long travelTime;

    @Schema(description = "Суммарное время от отправки до закрытия заказа")
    private Long totalTime;

    @Schema(description = "Скомбинированное расстояние. Расстояние пройденное совместно с другим заказом")
    private Double combinedDistance;
}
