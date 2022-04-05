package com.nocmok.orp.orp_solver.kafka.orp_input.mapper;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;

public class ServiceRequestMessageMapper {

    public ServiceRequestDispatchingService.ServiceRequestDto mapMessageToServiceRequestDispatchingServiceDto(ServiceRequestMessage message) {
        return ServiceRequestDispatchingService.ServiceRequestDto.builder()
                .requestId(message.getRequestId())
                .pickupNodeId(message.getPickupNodeId())
                .dropoffNodeId(message.getDropoffNodeId())
                .detourConstraint(message.getDetourConstraint())
                .maxPickupDelaySeconds(message.getMaxPickupDelaySeconds())
                .load(message.getLoad())
                .requestedAt(message.getRequestedAt())
                .build();
    }

    public ServiceRequestStorageService.ServiceRequestDto mapMessageToServiceRequestStorageServiceDto(ServiceRequestMessage message) {
        return ServiceRequestStorageService.ServiceRequestDto.builder()
                .requestId(message.getRequestId())
                .pickupNodeId(message.getPickupNodeId())
                .pickupLat(message.getPickupLat())
                .pickupLon(message.getPickupLon())
                .dropoffNodeId(message.getDropoffNodeId())
                .dropoffLat(message.getDropoffLat())
                .dropoffLon(message.getDropoffLon())
                .detourConstraint(message.getDetourConstraint())
                .maxPickupDelaySeconds(message.getMaxPickupDelaySeconds())
                .load(message.getLoad())
                .requestedAt(message.getRequestedAt())
                .build();
    }
}
