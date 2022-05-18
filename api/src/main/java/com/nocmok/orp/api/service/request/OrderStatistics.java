package com.nocmok.orp.api.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class OrderStatistics {

    private Double distanceScheduled;
    private Double distanceTravelled;
    private Double serviceDistance;
    private Long dispatchWaitingTime;
    private Long serviceWaitingTime;
    private Long pickupWaitingTime;
    private Long travelTime;
    private Long totalTime;
    private Double combinedDistance;
}
