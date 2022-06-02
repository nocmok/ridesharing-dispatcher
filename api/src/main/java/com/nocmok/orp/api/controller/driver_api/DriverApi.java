package com.nocmok.orp.api.controller.driver_api;

import com.nocmok.orp.api.controller.common_dto.ScheduleNode;
import com.nocmok.orp.api.controller.common_dto.SessionDto;
import com.nocmok.orp.api.controller.driver_api.dto.CreateSessionRequest;
import com.nocmok.orp.api.controller.driver_api.dto.CreateSessionResponse;
import com.nocmok.orp.api.controller.driver_api.dto.UpdateScheduleRequest;
import com.nocmok.orp.api.service.session.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Controller()
@RequestMapping("/driver_api/v0")
public class DriverApi {

    private SessionManagementService sessionManagementService;

    @Autowired
    public DriverApi(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    @PostMapping("/create_session")
    public @ResponseBody CreateSessionResponse createSession(@RequestBody CreateSessionRequest request) {
        var session = sessionManagementService.createSession(
                request.getCapacity(),
                request.getCoordinates().getLatitude(),
                request.getCoordinates().getLongitude(),
                request.getRoad().getSourceId(),
                request.getRoad().getTargetId()
        );
        return CreateSessionResponse.builder()
                .createdSession(SessionDto.builder()
                        .sessionId(session.getSessionId())
                        .capacity(session.getCapacity())
                        .residualCapacity(session.getResidualCapacity())
                        .schedule(session.getSchedule().stream().map(node -> ScheduleNode.builder()
                                .kind(node.getKind())
                                .orderId(node.getOrderId())
                                .nodeId(node.getNodeId())
                                .build()).collect(Collectors.toList()))
                        .build())
                .road(request.getRoad())
                .coordinates(request.getCoordinates())
                .build();
    }

    @PostMapping("/update_order_status")
    public @ResponseBody String completeScheduleCheckpoint(@RequestBody UpdateScheduleRequest request) {
        sessionManagementService.updateOrderStatus(request.getSessionId(), request.getOrderId(), request.getUpdatedStatus());
        return "{}";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public @ResponseBody String unknownExceptionHandler(Exception exception) {
        return exception.getMessage();
    }

}
