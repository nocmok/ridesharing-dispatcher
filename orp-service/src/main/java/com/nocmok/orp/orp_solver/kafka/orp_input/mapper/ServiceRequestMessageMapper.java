package com.nocmok.orp.orp_solver.kafka.orp_input.mapper;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;

public class ServiceRequestMessageMapper {

    public ServiceRequest mapMessageToServiceRequestDispatchingServiceDto(ServiceRequestMessage message) {
        return ServiceRequest.builder()
                .requestId(message.getRequestId())
                .recordedOriginLatitude(message.getRecordedOriginLatitude())
                .recordedOriginLongitude(message.getRecordedOriginLongitude())
                .recordedDestinationLatitude(message.getRecordedDestinationLatitude())
                .recordedDestinationLongitude(message.getRecordedDestinationLongitude())
                .pickupRoadSegmentStartNodeId(message.getPickupRoadSegmentStartNodeId())
                .pickupRoadSegmentEndNodeId(message.getPickupRoadSegmentEndNodeId())
                .dropOffRoadSegmentStartNodeId(message.getDropOffRoadSegmentStartNodeId())
                .dropOffRoadSegmentEndNodeId(message.getDropOffRoadSegmentEndNodeId())
                .load(message.getLoad())
                .detourConstraint(message.getDetourConstraint())
                .maxPickupDelaySeconds(message.getMaxPickupDelaySeconds())
                .requestedAt(message.getRequestedAt())
                .status(OrderStatus.PENDING)
                .build();

    }

    public ServiceRequest mapMessageToServiceRequestStorageServiceDto(ServiceRequestMessage message) {
        return ServiceRequest.builder()
                .requestId(message.getRequestId())
                .recordedOriginLatitude(message.getRecordedOriginLatitude())
                .recordedOriginLongitude(message.getRecordedOriginLongitude())
                .recordedDestinationLatitude(message.getRecordedDestinationLatitude())
                .recordedDestinationLongitude(message.getRecordedDestinationLongitude())
                .pickupRoadSegmentStartNodeId(message.getPickupRoadSegmentStartNodeId())
                .pickupRoadSegmentEndNodeId(message.getPickupRoadSegmentEndNodeId())
                .dropOffRoadSegmentStartNodeId(message.getDropOffRoadSegmentStartNodeId())
                .dropOffRoadSegmentEndNodeId(message.getDropOffRoadSegmentEndNodeId())
                .load(message.getLoad())
                .detourConstraint(message.getDetourConstraint())
                .maxPickupDelaySeconds(message.getMaxPickupDelaySeconds())
                .requestedAt(message.getRequestedAt())
                .status(OrderStatus.PENDING)
                .build();
    }
}
