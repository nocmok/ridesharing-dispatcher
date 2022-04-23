package com.nocmok.orp.simulator.service.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.kafka.orp_input.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("updatedStatus")
    private OrderStatus updatedStatus;
}
