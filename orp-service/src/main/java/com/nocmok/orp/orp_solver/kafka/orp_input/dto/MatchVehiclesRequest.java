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

    @JsonProperty("load")
    private int load;

    public MatchVehiclesRequest() {

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
