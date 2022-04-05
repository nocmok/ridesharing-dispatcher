package com.nocmok.orp.core_api;

import java.time.Instant;

public class Request {

    /**
     * Идентификатор запроса
     */
    private final String requestId;

    /**
     * Идентификатор вершины графа, привязанной к точке посадки
     */
    private final int pickupNodeId;

    /**
     * Идентификатор вершины графа, привязанной к точке высадки
     */
    private final int dropoffNodeId;

    /**
     * Время создания запроса
     */
    private final Instant requestedAt;

    /**
     * Ограничение на задержку связанную с применением райдшеринга.
     * Например, если
     * detourConstraint = 1.5
     * t = оценка времени кратчайшего маршрута от точки посадки до точки высадки
     * T = оценка времени построенного маршрута от точки посадки до точки высадки
     * то T <= detourConstraint * t
     */
    private final double detourConstraint;

    /**
     * Максимальное допустимое время ожидания тс от момента формирования запроса
     */
    private final int maxPickupDelaySeconds;

    /**
     * Нагрузка оказываемая на тс при выполнении запроса, например количество человек
     */
    private final int load;

    public Request(String requestId, int pickupNodeId, int dropoffNodeId, Instant requestedAt, double detourConstraint, int maxPickupDelaySeconds, int load) {
        this.requestId = requestId;
        this.pickupNodeId = pickupNodeId;
        this.dropoffNodeId = dropoffNodeId;
        this.requestedAt = requestedAt;
        this.detourConstraint = detourConstraint;
        this.maxPickupDelaySeconds = maxPickupDelaySeconds;
        this.load = load;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getPickupNodeId() {
        return pickupNodeId;
    }

    public int getDropoffNodeId() {
        return dropoffNodeId;
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
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", pickupNodeId=" + pickupNodeId +
                ", dropoffNodeId=" + dropoffNodeId +
                ", requestedAt=" + requestedAt +
                ", detourConstraint=" + detourConstraint +
                ", maxPickupDelaySeconds=" + maxPickupDelaySeconds +
                ", load=" + load +
                '}';
    }
}
