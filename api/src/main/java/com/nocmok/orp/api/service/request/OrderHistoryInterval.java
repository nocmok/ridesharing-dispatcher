package com.nocmok.orp.api.service.request;

import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class OrderHistoryInterval {

    @NonNull
    private Instant startTime;

    @NonNull
    private Instant endTime;

    @NonNull
    private Double distance;

    @NonNull
    private Integer companions;

    @NonNull
    private Integer companionOrders;

    @NonNull
    private OrderStatus status;
}
