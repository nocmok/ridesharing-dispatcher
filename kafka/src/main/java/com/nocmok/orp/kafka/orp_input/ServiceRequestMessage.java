package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

/**
 * Запрос поступивший от клиента на обработку
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestMessage {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("pickupNodeId")
    private Integer pickupNodeId;

    @JsonProperty("pickupLat")
    private Double pickupLat;

    @JsonProperty("pickupLon")
    private Double pickupLon;

    @JsonProperty("dropoffNodeId")
    private Integer dropoffNodeId;

    @JsonProperty("dropoffLat")
    private Double dropoffLat;

    @JsonProperty("dropoffLon")
    private Double dropoffLon;

    @JsonProperty("requestedAt")
    private Instant requestedAt;

    @JsonProperty("detourConstraint")
    private Double detourConstraint;

    @JsonProperty("maxPickupDelaySeconds")
    private Integer maxPickupDelaySeconds;

    @JsonProperty("load")
    private Integer load;

    @JsonProperty("topK")
    private Integer topK;
}
