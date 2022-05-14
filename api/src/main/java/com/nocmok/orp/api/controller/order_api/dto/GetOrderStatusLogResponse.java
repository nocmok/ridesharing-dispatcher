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
public class GetOrderStatusLogResponse {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("statusLog")
    private List<OrderStatusLogEntry> statusLog;

    @JsonGetter("size")
    public int size() {
        return statusLog == null ? 0 : statusLog.size();
    }
}
