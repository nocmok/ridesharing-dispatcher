package com.nocmok.orp.api.controller.rider_api;

import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestRequest;
import com.nocmok.orp.api.controller.rider_api.dto.CreateServiceRequestResponse;
import com.nocmok.orp.api.service.request_management.DispatchingService;
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
        var requestInfo = dispatchingService.dispatchRequest(RequestInfo.builder()
                .requestId(null)
                .recordedOrigin(request.getRecordedOrigin())
                .recordedDestination(request.getRecordedDestination())
                .pickupRoadSegment(request.getPickupRoadSegment())
                .dropoffRoadSegment(request.getDropoffRoadSegment())
                .detourConstraint(request.getDetourConstraint())
                .maxPickupDelaySeconds(request.getMaxPickupDelaySeconds())
                .load(request.getLoad())
                .requestedAt(request.getRequestedAt())
                .build());

        return CreateServiceRequestResponse.builder()
                .requestId(requestInfo.getRequestId())
                .recordedOrigin(requestInfo.getRecordedOrigin())
                .recordedDestination(requestInfo.getRecordedDestination())
                .pickupRoadSegment(requestInfo.getPickupRoadSegment())
                .dropoffRoadSegment(requestInfo.getDropoffRoadSegment())
                .detourConstraint(requestInfo.getDetourConstraint())
                .maxPickupDelaySeconds(requestInfo.getMaxPickupDelaySeconds())
                .load(requestInfo.getLoad())
                .requestedAt(requestInfo.getRequestedAt())
                .build();
    }
}
