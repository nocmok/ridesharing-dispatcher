package com.nocmok.orp.simulator.service.telemetry;

import java.time.Instant;

public class WalkingStub implements WalkStrategy {

    private final String sessionId;
    private final double sourceLat;
    private final double sourceLon;
    private final double targetLat;
    private final double targetLon;
    private final double timeToWalk;

    private double timeWalked = 0;

    public WalkingStub(String sessionId, double sourceLat, double sourceLon, double targetLat, double targetLon, double timeToWalk) {
        this.sessionId = sessionId;
        this.sourceLat = sourceLat;
        this.sourceLon = sourceLon;
        this.targetLat = targetLat;
        this.targetLon = targetLon;
        this.timeToWalk = timeToWalk;
    }

    @Override public Telemetry nextTelemetry(double time) {
        timeWalked += time;
        timeWalked = Double.min(timeToWalk, timeWalked);

        double progress = timeWalked / timeToWalk;
        double lat = sourceLat + progress * (targetLat - sourceLat);
        double lon = sourceLon + progress * (targetLon - sourceLon);

        return new Telemetry(sessionId, lat, lon, 0d, Instant.now());
    }
}
