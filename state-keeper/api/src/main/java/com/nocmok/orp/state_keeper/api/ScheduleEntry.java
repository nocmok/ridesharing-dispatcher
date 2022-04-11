package com.nocmok.orp.state_keeper.api;

import java.time.Instant;

public class ScheduleEntry {

    /**
     * Крайнее время прибытия в контрольную точку
     */
    private Instant deadline;
    /**
     * Дельта нагрузки на тс при прохождении контрольной точки
     */
    private Integer load;
    /**
     * Идентификатор вершины в графе к которой прикреплена контрольная точка
     */
    private String nodeId;
    /**
     * Широта координаты контрольной точки
     */
    private Double latitude;
    /**
     * Долгота координаты контрольной точки
     */
    private Double longitude;
    /**
     * Тип контрольной точки
     */
    private ScheduleEntryKind kind;
    /**
     * Идентификатор заказа прикрепленного к контрольной точке
     */
    private String orderId;

    public ScheduleEntry(Instant deadline, Integer load, String nodeId, Double latitude, Double longitude, ScheduleEntryKind kind, String orderId) {
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

    public ScheduleEntryKind getKind() {
        return kind;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override public String toString() {
        return "ScheduleEntry{" +
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
