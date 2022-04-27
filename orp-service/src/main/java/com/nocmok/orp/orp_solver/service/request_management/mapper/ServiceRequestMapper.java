package com.nocmok.orp.orp_solver.service.request_management.mapper;

import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
import com.nocmok.orp.orp_solver.storage.request_management.ServiceRequestStorage;
import org.springframework.stereotype.Component;

@Component("com.nocmok.orp.orp_solver.service.request_management.mapper.ServiceRequestMapper")
public class ServiceRequestMapper {

    public ServiceRequestStorage.ServiceRequestDto mapServiceDtoToStorageDto(ServiceRequestStorageServiceImpl.ServiceRequestDto serviceRequest) {
        return ServiceRequestStorage.ServiceRequestDto.builder()
                .requestId(serviceRequest.getRequestId())
                .recordedOriginLatitude(serviceRequest.getRecordedOriginLatitude())
                .recordedOriginLongitude(serviceRequest.getRecordedOriginLongitude())
                .recordedDestinationLatitude(serviceRequest.getRecordedDestinationLatitude())
                .recordedDestinationLongitude(serviceRequest.getRecordedDestinationLongitude())
                .pickupRoadSegmentStartNodeId(serviceRequest.getPickupRoadSegmentStartNodeId())
                .pickupRoadSegmentEndNodeId(serviceRequest.getPickupRoadSegmentEndNodeId())
                .dropOffRoadSegmentStartNodeId(serviceRequest.getDropOffRoadSegmentStartNodeId())
                .dropOffRoadSegmentEndNodeId(serviceRequest.getDropOffRoadSegmentEndNodeId())
                .load(serviceRequest.getLoad())
                .detourConstraint(serviceRequest.getDetourConstraint())
                .maxPickupDelaySeconds(serviceRequest.getMaxPickupDelaySeconds())
                .requestedAt(serviceRequest.getRequestedAt())
                .status(serviceRequest.getStatus())
                .build();
    }

    public ServiceRequestStorageServiceImpl.ServiceRequestDto mapStorageDtoToServiceDto(ServiceRequestStorage.ServiceRequestDto serviceRequest) {
        return ServiceRequestStorageServiceImpl.ServiceRequestDto.builder()
                .requestId(serviceRequest.getRequestId())
                .recordedOriginLatitude(serviceRequest.getRecordedOriginLatitude())
                .recordedOriginLongitude(serviceRequest.getRecordedOriginLongitude())
                .recordedDestinationLatitude(serviceRequest.getRecordedDestinationLatitude())
                .recordedDestinationLongitude(serviceRequest.getRecordedDestinationLongitude())
                .pickupRoadSegmentStartNodeId(serviceRequest.getPickupRoadSegmentStartNodeId())
                .pickupRoadSegmentEndNodeId(serviceRequest.getPickupRoadSegmentEndNodeId())
                .dropOffRoadSegmentStartNodeId(serviceRequest.getDropOffRoadSegmentStartNodeId())
                .dropOffRoadSegmentEndNodeId(serviceRequest.getDropOffRoadSegmentEndNodeId())
                .load(serviceRequest.getLoad())
                .detourConstraint(serviceRequest.getDetourConstraint())
                .maxPickupDelaySeconds(serviceRequest.getMaxPickupDelaySeconds())
                .requestedAt(serviceRequest.getRequestedAt())
                .status(serviceRequest.getStatus())
                .build();
    }
}
