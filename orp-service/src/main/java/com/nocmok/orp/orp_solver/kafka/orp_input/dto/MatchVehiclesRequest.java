package com.nocmok.orp.orp_solver.kafka.orp_input.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class MatchVehiclesRequest {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("pickupNodeId")
    private int pickupNodeId;

    @JsonProperty("pickupLat")
    private double pickupLat;

    @JsonProperty("pickupLon")
    private double pickupLon;

    @JsonProperty("dropoffNodeId")
    private int dropoffNodeId;

    @JsonProperty("dropoffLat")
    private double dropoffLat;

    @JsonProperty("dropoffLon")
    private double dropoffLon;

    @JsonProperty("requestedAt")
    private Instant requestedAt;

    @JsonProperty("detourConstraint")
    private double detourConstraint;

    @JsonProperty("maxPickupDelaySeconds")
    private int maxPickupDelaySeconds;

    @JsonProperty("load")
    private int load;

    @JsonProperty("topK")
    private int topK;

    public MatchVehiclesRequest() {

    }

    public int getTopK() {
        return topK;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getPickupNodeId() {
        return pickupNodeId;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public double getPickupLon() {
        return pickupLon;
    }

    public int getDropoffNodeId() {
        return dropoffNodeId;
    }

    public double getDropoffLat() {
        return dropoffLat;
    }

    public double getDropoffLon() {
        return dropoffLon;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public double getDetourConstraint() {
        return detourConstraint;
    }

    public int getMaxPickupDelaySeconds() {
        return maxPickupDelaySeconds;
    }

    public int getLoad() {
        return load;
    }

    @Override public String toString() {
        return "MatchVehiclesRequest{" +
                "requestId='" + requestId + '\'' +
                ", pickupNodeId=" + pickupNodeId +
                ", pickupLat=" + pickupLat +
                ", pickupLon=" + pickupLon +
                ", dropoffNodeId=" + dropoffNodeId +
                ", dropoffLat=" + dropoffLat +
                ", dropoffLon=" + dropoffLon +
                ", requestedAt=" + requestedAt +
                ", detourConstraint=" + detourConstraint +
                ", load=" + load +
                '}';
    }
}
