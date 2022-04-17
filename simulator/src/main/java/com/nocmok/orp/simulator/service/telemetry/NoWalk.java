package com.nocmok.orp.simulator.service.telemetry;

import java.time.Instant;

public class NoWalk implements WalkStrategy {

    private String sessionId;
    private double latitude;
    private double longitude;
    private double accuracy = 1;

    public NoWalk(String sessionId, double latitude, double longitude) {
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override public Telemetry nextTelemetry(double time) {
        return Telemetry.builder()
                .sessionId(sessionId)
                .latitude(latitude)
                .longitude(longitude)
                .accuracy(accuracy)
                .recordedAt(Instant.now())
                .build();
    }
}
