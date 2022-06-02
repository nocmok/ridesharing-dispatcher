package com.nocmok.orp.api.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class OrderExpenditureInterval {

    private Instant startTime;
    private Instant endTime;
    private Integer companions;
    private Double distanceTravelled;
}
