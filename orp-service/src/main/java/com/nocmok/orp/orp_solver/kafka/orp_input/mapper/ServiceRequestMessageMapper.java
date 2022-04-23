package com.nocmok.orp.orp_solver.kafka.orp_input.mapper;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.request_execution.OrderStatus;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;

public class ServiceRequestMessageMapper {

    public ServiceRequestDispatchingService.ServiceRequestDto mapMessageToServiceRequestDispatchingServiceDto(ServiceRequestMessage message) {
        return ServiceRequestDispatchingService.ServiceRequestDto.builder()
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

    public ServiceRequestStorageService.ServiceRequestDto mapMessageToServiceRequestStorageServiceDto(ServiceRequestMessage message) {
        return ServiceRequestStorageService.ServiceRequestDto.builder()
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
