package com.nocmok.orp.solver.api;

import java.time.Instant;

public class Request {

    /**
     * Идентификатор запроса
     */
    private final String requestId;

    /**
     * Дорожный сегмент с которого должна быть осуществлена посадка клиента.
     */
    private final RoadSegment pickupRoadSegment;

    /**
     * Дорожный сегмент с которого должна быть осуществлена высадка клиента.
     */
    private final RoadSegment dropOffRoadSegment;

    /**
     * Фактические координаты запроса, записанные с устройства, которое отправило запрос
     */
    private final Double recordedOriginLatitude;

    /**
     * Фактические координаты запроса, записанные с устройства, которое отправило запрос
     */
    private final Double recordedOriginLongitude;

    private final Double recordedDestinationLatitude;

    private final Double recordedDestinationLongitude;

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
    private final Double detourConstraint;

    /**
     * Максимальное допустимое время ожидания тс от момента формирования запроса
     */
    private final Integer maxPickupDelaySeconds;

    /**
     * Нагрузка оказываемая на тс при выполнении запроса, например количество человек
     */
    private final Integer load;

    public Request(String requestId, RoadSegment pickupRoadSegment, RoadSegment dropOffRoadSegment, Double recordedOriginLatitude,
                   Double recordedOriginLongitude, Double recordedDestinationLatitude, Double recordedDestinationLongitude, Instant requestedAt,
                   Double detourConstraint, Integer maxPickupDelaySeconds, Integer load) {
        this.requestId = requestId;
        this.pickupRoadSegment = pickupRoadSegment;
        this.dropOffRoadSegment = dropOffRoadSegment;
        this.recordedOriginLatitude = recordedOriginLatitude;
        this.recordedOriginLongitude = recordedOriginLongitude;
        this.recordedDestinationLatitude = recordedDestinationLatitude;
        this.recordedDestinationLongitude = recordedDestinationLongitude;
        this.requestedAt = requestedAt;
        this.detourConstraint = detourConstraint;
        this.maxPickupDelaySeconds = maxPickupDelaySeconds;
        this.load = load;
    }

    public String getRequestId() {
        return requestId;
    }

    public RoadSegment getPickupRoadSegment() {
        return pickupRoadSegment;
    }

    public RoadSegment getDropOffRoadSegment() {
        return dropOffRoadSegment;
    }

    public Double getRecordedOriginLatitude() {
        return recordedOriginLatitude;
    }

    public Double getRecordedOriginLongitude() {
        return recordedOriginLongitude;
    }

    public Double getRecordedDestinationLatitude() {
        return recordedDestinationLatitude;
    }

    public Double getRecordedDestinationLongitude() {
        return recordedDestinationLongitude;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Double getDetourConstraint() {
        return detourConstraint;
    }

    public Integer getMaxPickupDelaySeconds() {
        return maxPickupDelaySeconds;
    }

    public Integer getLoad() {
        return load;
    }

    @Override public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", pickupRoadSegment=" + pickupRoadSegment +
                ", dropOffRoadSegment=" + dropOffRoadSegment +
                ", recordedOriginLatitude=" + recordedOriginLatitude +
                ", recordedOriginLongitude=" + recordedOriginLongitude +
                ", recordedDestinationLatitude=" + recordedDestinationLatitude +
                ", recordedDestinationLongitude=" + recordedDestinationLongitude +
                ", requestedAt=" + requestedAt +
                ", detourConstraint=" + detourConstraint +
                ", maxPickupDelaySeconds=" + maxPickupDelaySeconds +
                ", load=" + load +
                '}';
    }
}
