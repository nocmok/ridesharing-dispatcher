package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetOrderExpendituresResponse {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("summary")
    private OrderExpendituresSummary summary;
}
