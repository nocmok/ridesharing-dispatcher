package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderStatusMessage {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("updatedStatus")
    private OrderStatus updatedStatus;
}
