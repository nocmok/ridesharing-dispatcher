package com.nocmok.orp.orp_solver.service.request_management.mapper;

import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
import com.nocmok.orp.orp_solver.storage.request_management.ServiceRequestStorage;
import org.springframework.stereotype.Component;

@Component("com.nocmok.orp.orp_solver.service.request_management.mapper.ServiceRequestMapper")
public class ServiceRequestMapper {

    public ServiceRequestStorage.ServiceRequestDto mapServiceDtoToStorageDto(ServiceRequestStorageService.ServiceRequestDto serviceRequestDto) {
        return ServiceRequestStorage.ServiceRequestDto.builder()
                .detourConstraint(serviceRequestDto.getDetourConstraint())
                .requestId(serviceRequestDto.getRequestId())
                .dropoffLat(serviceRequestDto.getDropoffLat())
                .dropoffLon(serviceRequestDto.getDropoffLon())
                .dropoffNodeId(serviceRequestDto.getDropoffNodeId())
                .load(serviceRequestDto.getLoad())
                .maxPickupDelaySeconds(serviceRequestDto.getMaxPickupDelaySeconds())
                .pickupLat(serviceRequestDto.getPickupLat())
                .pickupLon(serviceRequestDto.getPickupLon())
                .requestedAt(serviceRequestDto.getRequestedAt())
                .pickupNodeId(serviceRequestDto.getPickupNodeId())
                .build();
    }

    public ServiceRequestStorageService.ServiceRequestDto mapStorageDtoToServiceDto(ServiceRequestStorage.ServiceRequestDto serviceRequestDto) {
        return ServiceRequestStorageService.ServiceRequestDto.builder()
                .detourConstraint(serviceRequestDto.getDetourConstraint())
                .requestId(serviceRequestDto.getRequestId())
                .dropoffLat(serviceRequestDto.getDropoffLat())
                .dropoffLon(serviceRequestDto.getDropoffLon())
                .dropoffNodeId(serviceRequestDto.getDropoffNodeId())
                .load(serviceRequestDto.getLoad())
                .maxPickupDelaySeconds(serviceRequestDto.getMaxPickupDelaySeconds())
                .pickupLat(serviceRequestDto.getPickupLat())
                .pickupLon(serviceRequestDto.getPickupLon())
                .requestedAt(serviceRequestDto.getRequestedAt())
                .pickupNodeId(serviceRequestDto.getPickupNodeId())
                .build();
    }
}
