package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetOrderHistoryRequest {

    @JsonProperty("orderId")
    private String orderId;
}
