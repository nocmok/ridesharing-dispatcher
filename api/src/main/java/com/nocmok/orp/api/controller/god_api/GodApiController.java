package com.nocmok.orp.api.controller.god_api;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.Node;
import com.nocmok.orp.api.controller.common_dto.RoadSegmentWithGeodata;
import com.nocmok.orp.api.controller.common_dto.ScheduleNode;
import com.nocmok.orp.api.controller.common_dto.SessionInfo;
import com.nocmok.orp.api.controller.god_api.dto.GetActiveRequestIdsRequest;
import com.nocmok.orp.api.controller.god_api.dto.GetActiveRequestIdsResponse;
import com.nocmok.orp.api.controller.god_api.dto.GetActiveSessionsIdsRequest;
import com.nocmok.orp.api.controller.god_api.dto.GetActiveSessionsIdsResponse;
import com.nocmok.orp.api.controller.god_api.dto.GetSessionInfoRequest;
import com.nocmok.orp.api.controller.god_api.dto.GetSessionInfoResponse;
import com.nocmok.orp.api.controller.god_api.dto.GetSessionsGeodataRequest;
import com.nocmok.orp.api.controller.god_api.dto.GetSessionsGeodataResponse;
import com.nocmok.orp.api.controller.god_api.dto.SessionGeodata;
import com.nocmok.orp.api.service.geo.GeolocationService;
import com.nocmok.orp.api.service.request_management.RequestService;
import com.nocmok.orp.api.service.session_management.SessionManagementService;
import com.nocmok.orp.api.storage.route_cache.RouteNode;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/god_api/v0")
public class GodApiController {

    private SessionManagementService sessionManagementService;
    private GeolocationService geolocationService;
    private RequestService requestService;

    @Autowired
    public GodApiController(SessionManagementService sessionManagementService, GeolocationService geolocationService,
                            RequestService requestService) {
        this.sessionManagementService = sessionManagementService;
        this.geolocationService = geolocationService;
        this.requestService = requestService;
    }

    private ScheduleNode mapInternalScheduleNodeToApiScheduleNode(ScheduleEntry scheduleEntry) {
        return ScheduleNode.builder()
                .orderId(scheduleEntry.getOrderId())
                .kind(scheduleEntry.getKind())
                .nodeId(scheduleEntry.getNodeId())
                .build();
    }

    @PostMapping("/session/active_sessions/ids")
    public @ResponseBody GetActiveSessionsIdsResponse getActiveSessionsIds(@RequestBody GetActiveSessionsIdsRequest request) {
        return GetActiveSessionsIdsResponse.builder()
                .activeSessionsIds(sessionManagementService.getActiveSessionsIds())
                .build();
    }


    private Node mapGraphApiNodeToApiNode(com.nocmok.orp.graph.api.Node node) {
        return Node.builder()
                .id(node.getId())
                .coordinates(new Coordinates(node.getLatitude(), node.getLongitude()))
                .build();
    }

    private RoadSegmentWithGeodata mapGraphApiRoadSegmentToApiRoadSegment(Segment segment) {
        return RoadSegmentWithGeodata.builder()
                .source(mapGraphApiNodeToApiNode(segment.getStartNode()))
                .target(mapGraphApiNodeToApiNode(segment.getEndNode()))
                .build();
    }

    @PostMapping("/session/geodata")
    public @ResponseBody GetSessionsGeodataResponse getSessionsGeodata(@RequestBody GetSessionsGeodataRequest request) {
        return GetSessionsGeodataResponse.builder()
                .sessions(geolocationService.getSessionsGeodata(request.getSessionIds()).stream()
                        .map(graphObject -> SessionGeodata.builder()
                                .sessionId(graphObject.getId())
                                .coordinates(new Coordinates(graphObject.getLatitude(), graphObject.getLongitude()))
                                .roadSegment(mapGraphApiRoadSegmentToApiRoadSegment(graphObject.getSegment()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @PostMapping("/request/active_request/ids")
    public @ResponseBody GetActiveRequestIdsResponse getActiveRequestIds(@RequestBody GetActiveRequestIdsRequest request) {
        return GetActiveRequestIdsResponse.builder()
                .requestIds(requestService.getActiveRequestIds())
                .build();
    }


    private Node mapInternalRouteNodeToApiNode(RouteNode routeNode) {
        return new Node(routeNode.getNodeId(), new Coordinates(routeNode.getLatitude(), routeNode.getLongitude()));
    }

    @PostMapping("/session/info")
    public @ResponseBody GetSessionInfoResponse getSessionInfo(@RequestBody GetSessionInfoRequest request) {
        var sessionInfo = sessionManagementService.getActiveSessionInfo(request.getSessionId());
        return GetSessionInfoResponse.builder()
                .sessionInfo(SessionInfo.builder()
                        .id(sessionInfo.getId())
                        .schedule(sessionInfo.getSchedule().stream()
                                .map(this::mapInternalScheduleNodeToApiScheduleNode)
                                .collect(Collectors.toList()))
                        .routeScheduled(sessionInfo.getRouteScheduled().stream()
                                .map(this::mapInternalRouteNodeToApiNode)
                                .collect(Collectors.toList()))
                        .capacity(sessionInfo.getCapacity())
                        .residualCapacity(sessionInfo.getResidualCapacity())
                        .sessionStatus(sessionInfo.getStatus())
                        .build())
                .build();
    }
}
