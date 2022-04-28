package com.nocmok.orp.api.controller.rider_api;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestRequest;
import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestResponse;
import com.nocmok.orp.api.service.request_management.DispatchingService;
import com.nocmok.orp.api.storage.request_management.dto.LatLon;
import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;
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
        var requestInfo = RequestInfo.builder()
                .requestId(null)
                .recordedOrigin(new LatLon(request.getRecordedOrigin().getLatitude(), request.getRecordedOrigin().getLongitude()))
                .recordedDestination(new LatLon(request.getRecordedDestination().getLatitude(), request.getRecordedDestination().getLongitude()))
                .pickupRoadSegment(new com.nocmok.orp.api.storage.request_management.dto.RoadSegment(request.getPickupRoadSegment().getSourceId(),
                        request.getPickupRoadSegment().getTargetId()))
                .dropoffRoadSegment(new com.nocmok.orp.api.storage.request_management.dto.RoadSegment(request.getDropoffRoadSegment().getSourceId(),
                        request.getDropoffRoadSegment().getTargetId()))
                .detourConstraint(request.getDetourConstraint())
                .maxPickupDelaySeconds(request.getMaxPickupDelaySeconds())
                .load(request.getLoad())
                .requestedAt(request.getRequestedAt())
                .build();

        requestInfo = dispatchingService.dispatchRequest(requestInfo);

        return CreateServiceRequestResponse.builder()
                .requestId(requestInfo.getRequestId())
                .recordedOrigin(new Coordinates(requestInfo.getRecordedOrigin().getLatitude(), requestInfo.getRecordedOrigin().getLatitude()))
                .recordedDestination(new Coordinates(requestInfo.getRecordedDestination().getLatitude(), requestInfo.getRecordedDestination().getLongitude()))
                .pickupRoadSegment(new com.nocmok.orp.api.controller.common_dto.RoadSegment(requestInfo.getPickupRoadSegment().getSourceId(),
                        requestInfo.getPickupRoadSegment().getTargetId()))
                .dropoffRoadSegment(new com.nocmok.orp.api.controller.common_dto.RoadSegment(requestInfo.getDropoffRoadSegment().getSourceId(),
                        requestInfo.getDropoffRoadSegment().getTargetId()))
                .detourConstraint(requestInfo.getDetourConstraint())
                .maxPickupDelaySeconds(requestInfo.getMaxPickupDelaySeconds())
                .load(requestInfo.getLoad())
                .requestedAt(requestInfo.getRequestedAt())
                .build();
    }
}
