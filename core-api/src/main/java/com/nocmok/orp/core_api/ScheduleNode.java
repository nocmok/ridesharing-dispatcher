package com.nocmok.orp.core_api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ScheduleNode {

    /**
     * Крайнее время прибытия в контрольную точку
     */
    @JsonProperty("deadline")
    private Instant deadline;
    /**
     * Дельта нагрузки на тс при прохождении контрольной точки
     */
    @JsonProperty("load")
    private int load;
    /**
     * Идентификатор вершины в графе к которой прикреплена контрольная точка
     */
    @JsonProperty("nodeId")
    private int nodeId;
    /**
     * Широта координаты контрольной точки
     */
    @JsonProperty("latitude")
    private double lat;
    /**
     * Долгота координаты контрольной точки
     */
    @JsonProperty("longitude")
    private double lon;
    /**
     * Тип контрольной точки
     */
    @JsonProperty("kind")
    private ScheduleNodeKind kind;
    /**
     * Идентификатор заказа прикрепленного к контрольной точке
     */
    @JsonProperty("orderId")
    private String orderId;

    public ScheduleNode() {

    }

    public ScheduleNode(Instant deadline, int load, int nodeId, double lat, double lon, ScheduleNodeKind kind, String orderId) {
        this.deadline = deadline;
        this.load = load;
        this.nodeId = nodeId;
        this.lat = lat;
        this.lon = lon;
        this.kind = kind;
        this.orderId = orderId;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public int getLoad() {
        return load;
    }

    public int getNodeId() {
        return nodeId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
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
                ", nodeId=" + nodeId +
                ", lat=" + lat +
                ", lon=" + lon +
                ", kind=" + kind +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
