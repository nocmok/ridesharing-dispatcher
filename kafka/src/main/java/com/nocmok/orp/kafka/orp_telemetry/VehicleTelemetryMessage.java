package com.nocmok.orp.kafka.orp_telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class VehicleTelemetryMessage {
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("accuracy")
    private double accuracy;

    @JsonProperty("recordedAt")
    private Instant recordedAt;

    public VehicleTelemetryMessage(String sessionId, double lat, double lon, double accuracy, Instant recordedAt) {
        this.sessionId = sessionId;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.recordedAt = recordedAt;
    }

    public VehicleTelemetryMessage() {

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
