package com.nocmok.orp.orp_solver.service.dispatching.mapper;

import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
import org.springframework.stereotype.Component;

@Component("com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper")
public class ServiceRequestMapper {

    public Request mapServiceDtoToRequest(ServiceRequestDispatchingService.ServiceRequestDto serviceRequestServiceDto) {
        return new Request(
                serviceRequestServiceDto.getRequestId(),
                serviceRequestServiceDto.getPickupNodeId(),
                serviceRequestServiceDto.getDropoffNodeId(),
                serviceRequestServiceDto.getRequestedAt(),
                serviceRequestServiceDto.getDetourConstraint(),
                serviceRequestServiceDto.getMaxPickupDelaySeconds(),
                serviceRequestServiceDto.getLoad()
        );
    }

    public Request mapServiceDtoToRequest(ServiceRequestStorageService.ServiceRequestDto serviceRequestServiceDto) {
        return new Request(
                serviceRequestServiceDto.getRequestId(),
                serviceRequestServiceDto.getPickupNodeId(),
                serviceRequestServiceDto.getDropoffNodeId(),
                serviceRequestServiceDto.getRequestedAt(),
                serviceRequestServiceDto.getDetourConstraint(),
                serviceRequestServiceDto.getMaxPickupDelaySeconds(),
                serviceRequestServiceDto.getLoad()
        );
    }
}
