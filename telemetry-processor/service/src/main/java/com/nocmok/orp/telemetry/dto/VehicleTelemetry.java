package com.nocmok.orp.telemetry.dto;

import java.time.Instant;

public class VehicleTelemetry {

    private String sessionId;

    private double lat;

    private double lon;

    private double accuracy;

    private Instant recordedAt;

    public VehicleTelemetry(String sessionId, double lat, double lon, double accuracy, Instant recordedAt) {
        this.sessionId = sessionId;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.recordedAt = recordedAt;
    }

    public VehicleTelemetry() {

    }

    public String getSessionId() {
        return sessionId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    @Override public String toString() {
        return "VehicleTelemetry{" +
                "sessionId='" + sessionId + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", accuracy=" + accuracy +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
