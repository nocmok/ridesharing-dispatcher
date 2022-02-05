package com.nocmok.orp.core_api;

import java.time.Instant;

public class Request {

    /**
     * Идентификатор запроса
     */
    private String requestId;

    /**
     * Идентификатор вершины графа, привязанной к точке посадки
     */
    private int pickupNodeId;

    /**
     * Широта координаты точки посадки
     */
    private double pickupLat;

    /**
     * Долгота координаты точки посадки
     */
    private double pickupLon;

    /**
     * Идентификатор вершины графа, привязанной к точке высадки
     */
    private int dropoffNodeId;

    /**
     * Широта координаты точки высадки
     */
    private double dropoffLat;

    /**
     * Долгота координаты точки высадки
     */
    private double dropoffLon;

    /**
     * Время создания запроса
     */
    private Instant requestedAt;

    /**
     * Ограничение на задержку связанную с применением райдшеринга.
     * Например, если
     * detourConstraint = 1.5
     * t = оценка времени кратчайшего маршрута от точки посадки до точки высадки
     * T = оценка времени построенного маршрута от точки посадки до точки высадки
     * то T <= detourConstraint * t
     */
    private double detourConstraint;

    /**
     * Нагрузка оказываемая на тс при выполнении запроса, например количество человек
     */
    private int load;

    public Request(String requestId, int pickupNodeId, double pickupLat, double pickupLon, int dropoffNodeId, double dropoffLat, double dropoffLon,
                   Instant requestedAt, double detourConstraint, int load) {
        this.requestId = requestId;
        this.pickupNodeId = pickupNodeId;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.dropoffNodeId = dropoffNodeId;
        this.dropoffLat = dropoffLat;
        this.dropoffLon = dropoffLon;
        this.requestedAt = requestedAt;
        this.detourConstraint = detourConstraint;
        this.load = load;
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

    public int getLoad() {
        return load;
    }
}
