package com.nocmok.orp.orp_solver.service.dispatching.mapper;

import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.solver.api.Request;
import com.nocmok.orp.solver.api.RoadSegment;
import org.springframework.stereotype.Component;

@Component("com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper")
public class ServiceRequestMapper {

    public Request mapServiceDtoToRequest(ServiceRequest serviceRequest) {
        return new Request(
                serviceRequest.getRequestId(),
                new RoadSegment(serviceRequest.getPickupRoadSegmentStartNodeId(), serviceRequest.getPickupRoadSegmentEndNodeId()),
                new RoadSegment(serviceRequest.getDropOffRoadSegmentStartNodeId(), serviceRequest.getDropOffRoadSegmentEndNodeId()),
                serviceRequest.getRecordedOriginLatitude(),
                serviceRequest.getRecordedOriginLongitude(),
                serviceRequest.getRecordedDestinationLatitude(),
                serviceRequest.getRecordedDestinationLongitude(),
                serviceRequest.getRequestedAt(),
                serviceRequest.getDetourConstraint(),
                serviceRequest.getMaxPickupDelaySeconds(),
                serviceRequest.getLoad()
        );
    }
}
