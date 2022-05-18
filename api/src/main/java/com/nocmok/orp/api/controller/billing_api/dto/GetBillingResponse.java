package com.nocmok.orp.api.controller.billing_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetBillingResponse {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("discountMeters")
    private Double discountMeters;

    @JsonProperty("metersToPayBeforeDiscount")
    private Double metersToPayBeforeDiscount;

    @JsonGetter("metersToPayAfterDiscount")
    private Double metersToPayAfterDiscount() {
        if (metersToPayBeforeDiscount == null || discountMeters == null) {
            return null;
        }
        return metersToPayBeforeDiscount - discountMeters;
    }
}
