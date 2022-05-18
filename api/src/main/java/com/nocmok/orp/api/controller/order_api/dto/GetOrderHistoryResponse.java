package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetOrderHistoryResponse {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("history")
    private List<OrderExecutionInterval> history;

    @JsonGetter("size")
    private Integer size() {
        return history == null ? 0 : history.size();
    }
}
