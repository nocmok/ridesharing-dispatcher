package com.nocmok.orp.solver.ls;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.solver.api.Request;

import java.time.Instant;

/**
 * Запрос от клиента, обогащенный геоданными
 */
class ExtendedRequest {

    private Request underlyingRequest;

    private Segment pickupRoadSegment;

    private Segment dropOffRoadSegment;

    private Double timeOnPickupRoadSegment;

    private Double timeOnDropOffRoadSegment;

    public ExtendedRequest(Request underlyingRequest, Segment pickupRoadSegment, Segment dropOffRoadSegment, Double timeOnPickupRoadSegment,
                           Double timeOnDropOffRoadSegment) {
        this.underlyingRequest = underlyingRequest;
        this.pickupRoadSegment = pickupRoadSegment;
        this.dropOffRoadSegment = dropOffRoadSegment;
        this.timeOnPickupRoadSegment = timeOnPickupRoadSegment;
        this.timeOnDropOffRoadSegment = timeOnDropOffRoadSegment;
    }

    public Instant getRequestedAt() {
        return underlyingRequest.getRequestedAt();
    }

    public Integer getMaxPickupDelaySeconds() {
        return underlyingRequest.getMaxPickupDelaySeconds();
    }

    public Integer getLoad() {
        return underlyingRequest.getLoad();
    }

    public String getOriginNodeId() {
        return underlyingRequest.getPickupRoadSegment().getStartNodeId();
    }

    public String getDestinationNodeId() {
        return underlyingRequest.getDropOffRoadSegment().getStartNodeId();
    }

    public String getRequestId() {
        return underlyingRequest.getRequestId();
    }

    public Double getDetourConstraint() {
        return underlyingRequest.getDetourConstraint();
    }

    public Segment getPickupRoadSegment() {
        return pickupRoadSegment;
    }

    public Segment getDropOffRoadSegment() {
        return dropOffRoadSegment;
    }

    public Double getRecordedOriginLatitude() {
        return underlyingRequest.getRecordedOriginLatitude();
    }

    public Double getRecordedOriginLongitude() {
        return underlyingRequest.getRecordedOriginLongitude();
    }

    public Double getRecordedDestinationLatitude() {
        return underlyingRequest.getRecordedDestinationLatitude();
    }

    public Double getRecordedDestinationLongitude() {
        return underlyingRequest.getRecordedDestinationLongitude();
    }

    /**
     * Сколько времени будет затрачено на участке дороги с которого будет осуществлена посадка клиента.
     */
    public Double getTimeOnPickupRoadSegment() {
        return this.timeOnPickupRoadSegment;
    }

    /**
     * Сколько времени будет затрачено на участке дороги с которого будет осуществлена высадка клиента.
     */
    public Double getTimeOnDropOffRoadSegment() {
        return this.timeOnDropOffRoadSegment;
    }
}
