package com.nocmok.orp.core_api;

import java.time.Instant;

public class ScheduleNode {

    /**
     * Крайнее время прибытия в контрольную точку
     */
    private Instant deadline;
    /**
     * Дельта нагрузки на тс при прохождении контрольной точки
     */
    private int load;
    /**
     * Идентификатор вершины в графе к которой прикреплена контрольная точка
     */
    private int nodeId;
    /**
     * Широта координаты контрольной точки
     */
    private double lat;
    /**
     * Долгота координаты контрольной точки
     */
    private double lon;
    /**
     * Тип контрольной точки
     */
    private ScheduleNodeKind kind;
    /**
     * Идентификатор заказа прикрепленного к контрольной точке
     */
    private String orderId;

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
}
