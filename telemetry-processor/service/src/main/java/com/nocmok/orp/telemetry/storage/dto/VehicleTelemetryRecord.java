package com.nocmok.orp.telemetry.storage.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public class VehicleTelemetryRecord {

    private String sessionId;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Instant recordedAt;

    public VehicleTelemetryRecord(String sessionId, Double latitude, Double longitude, Double accuracy, Instant recordedAt) {
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.recordedAt = recordedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    @Override public String toString() {
        return "VehicleTelemetryRecord{" +
                "sessionId='" + sessionId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
