package com.nocmok.orp.solver.api;

import java.time.Instant;

public class ScheduleNode {

    private Instant deadline;
    private Integer load;
    private String nodeId;
    private Double latitude;
    private Double longitude;
    private ScheduleNodeKind kind;
    private String orderId;

    public ScheduleNode(Instant deadline, Integer load, String nodeId, Double latitude, Double longitude, ScheduleNodeKind kind, String orderId) {
        this.deadline = deadline;
        this.load = load;
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kind = kind;
        this.orderId = orderId;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public Integer getLoad() {
        return load;
    }

    public String getNodeId() {
        return nodeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public ScheduleNodeKind getKind() {
        return kind;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override public String toString() {
        return "ScheduleNode{" +
                "deadline=" + deadline +
                ", load=" + load +
                ", nodeId='" + nodeId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", kind=" + kind +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
