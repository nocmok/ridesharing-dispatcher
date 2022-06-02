package com.nocmok.orp.notifier.service.telemetry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class VehicleGPSMessageServiceDto {

    private String sessionId;
    private Double latitude;
    private Double longitude;
    private Instant recordedAt;

}
