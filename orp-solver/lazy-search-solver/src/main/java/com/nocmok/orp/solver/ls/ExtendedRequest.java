package com.nocmok.orp.solver.ls;

import com.nocmok.orp.core_api.Request;

import java.time.Instant;

/**
 * Запрос от клиента, обогащенный геоданными
 */
class ExtendedRequest {

    private Request underlyingRequest;

    /**
     * Широта координаты точки посадки
     */
    private Double pickupLatitude;

    /**
     * Долгота координаты точки посадки
     */
    private Double pickupLongitude;

    /**
     * Широта координаты точки высадки
     */
    private Double dropoffLatitude;

    /**
     * Долгота координаты точки высадки
     */
    private Double dropoffLongitude;

    public ExtendedRequest(Request underlyingRequest) {
        this.underlyingRequest = underlyingRequest;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public void setDropoffLatitude(Double dropoffLatitude) {
        this.dropoffLatitude = dropoffLatitude;
    }

    public void setDropoffLongitude(Double dropoffLongitude) {
        this.dropoffLongitude = dropoffLongitude;
    }

    public int getMaxPickupDelaySeconds() {
        return underlyingRequest.getMaxPickupDelaySeconds();
    }

    public String getRequestId() {
        return underlyingRequest.getRequestId();
    }

    public int getPickupNodeId() {
        return underlyingRequest.getPickupNodeId();
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public int getDropoffNodeId() {
        return underlyingRequest.getDropoffNodeId();
    }

    public double getDropoffLatitude() {
        return dropoffLatitude;
    }

    public double getDropoffLongitude() {
        return dropoffLongitude;
    }

    public Instant getRequestedAt() {
        return underlyingRequest.getRequestedAt();
    }

    public double getDetourConstraint() {
        return underlyingRequest.getDetourConstraint();
    }

    public int getLoad() {
        return underlyingRequest.getLoad();
    }

    public Request getUnderlyingRequest() {
        return underlyingRequest;
    }
}
