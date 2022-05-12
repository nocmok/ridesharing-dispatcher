package com.nocmok.orp.api.controller.rider_api;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.RoadSegment;
import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestRequest;
import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestResponse;
import com.nocmok.orp.api.service.request_management.DispatchingService;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rider_api/v0")
public class RiderApi {

    private DispatchingService dispatchingService;

    @Autowired
    public RiderApi(DispatchingService dispatchingService) {
        this.dispatchingService = dispatchingService;
    }

    @PostMapping("/create_service_request")
    public @ResponseBody CreateServiceRequestResponse createServiceRequest(@RequestBody CreateServiceRequestRequest request) {
        var serviceRequest = ServiceRequest.builder()
                .requestId(null)
                .recordedOriginLatitude(request.getRecordedOrigin().getLatitude())
                .recordedOriginLongitude(request.getRecordedOrigin().getLongitude())
                .recordedDestinationLatitude(request.getRecordedDestination().getLatitude())
                .recordedDestinationLongitude(request.getRecordedDestination().getLongitude())
                .pickupRoadSegmentStartNodeId(request.getPickupRoadSegment().getSourceId())
                .pickupRoadSegmentEndNodeId(request.getPickupRoadSegment().getTargetId())
                .dropOffRoadSegmentStartNodeId(request.getDropoffRoadSegment().getSourceId())
                .dropOffRoadSegmentEndNodeId(request.getDropoffRoadSegment().getTargetId())
                .detourConstraint(request.getDetourConstraint())
                .maxPickupDelaySeconds(request.getMaxPickupDelaySeconds())
                .load(request.getLoad())
                .requestedAt(request.getRequestedAt())
                .build();

        serviceRequest = dispatchingService.dispatchRequest(serviceRequest);

        return CreateServiceRequestResponse.builder()
                .requestId(serviceRequest.getRequestId())
                .recordedOrigin(new Coordinates(serviceRequest.getRecordedOriginLatitude(), serviceRequest.getRecordedOriginLongitude()))
                .recordedDestination(new Coordinates(serviceRequest.getRecordedDestinationLatitude(), serviceRequest.getRecordedDestinationLongitude()))
                .pickupRoadSegment(new RoadSegment(serviceRequest.getPickupRoadSegmentStartNodeId(), serviceRequest.getPickupRoadSegmentEndNodeId()))
                .dropoffRoadSegment(new RoadSegment(serviceRequest.getDropOffRoadSegmentStartNodeId(), serviceRequest.getDropOffRoadSegmentEndNodeId()))
                .detourConstraint(serviceRequest.getDetourConstraint())
                .maxPickupDelaySeconds(serviceRequest.getMaxPickupDelaySeconds())
                .load(serviceRequest.getLoad())
                .requestedAt(serviceRequest.getRequestedAt())
                .build();
    }
}
