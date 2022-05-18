package com.nocmok.orp.api.controller.billing_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetBillingRequest {

    @JsonProperty("orderId")
    private String orderId;
}
