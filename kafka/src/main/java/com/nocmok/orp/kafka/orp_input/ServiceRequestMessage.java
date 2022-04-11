package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

/**
 * Запрос поступивший от клиента на обработку
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestMessage {

    @JsonProperty("requestId")
    private String requestId;

    /**
     * Фактические координаты указанные клиентом
     */
    @JsonProperty("recordedOriginLatitude")
    private Double recordedOriginLatitude;

    /**
     * Фактические координаты указанные клиентом
     */
    @JsonProperty("recordedOriginLongitude")
    private Double recordedOriginLongitude;

    /**
     * Фактические координаты указанные клиентом
     */
    @JsonProperty("recordedDestinationLatitude")
    private Double recordedDestinationLatitude;

    /**
     * Фактические координаты указанные клиентом
     */
    @JsonProperty("recordedDestinationLongitude")
    private Double recordedDestinationLongitude;

    @JsonProperty("pickupRoadSegmentStartNodeId")
    private String pickupRoadSegmentStartNodeId;

    @JsonProperty("pickupRoadSegmentEndNodeId")
    private String pickupRoadSegmentEndNodeId;

    @JsonProperty("dropOffRoadSegmentStartNodeId")
    private String dropOffRoadSegmentStartNodeId;

    @JsonProperty("dropOffRoadSegmentEndNodeId")
    private String dropOffRoadSegmentEndNodeId;

    @JsonProperty("requestedAt")
    private Instant requestedAt;

    /**
     * Ограничение на задержку вызванную применением райдшеринга.
     * Например, если
     * detourConstraint = 1.5
     * t = оценка времени кратчайшего маршрута от точки посадки до точки высадки
     * T = оценка времени построенного маршрута от точки посадки до точки высадки
     * то T <= detourConstraint * t
     */
    @JsonProperty("detourConstraint")
    private Double detourConstraint;

    @JsonProperty("maxPickupDelaySeconds")
    private Integer maxPickupDelaySeconds;

    @JsonProperty("load")
    private Integer load;
}
