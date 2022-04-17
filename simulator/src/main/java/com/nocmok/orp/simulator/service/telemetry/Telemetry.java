package com.nocmok.orp.simulator.service.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telemetry {

    @JsonProperty("sessionId")
    private String sessionId;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("lon")
    private Double longitude;
    @JsonProperty("accuracy")
    private Double accuracy;
    @JsonProperty("recordedAt")
    private Instant recordedAt;
}
