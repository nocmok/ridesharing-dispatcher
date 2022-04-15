package com.nocmok.orp.api.controller.driver_api;

import com.nocmok.orp.api.controller.driver_api.dto.Coordinates;
import com.nocmok.orp.api.controller.driver_api.dto.CreateSessionRequest;
import com.nocmok.orp.api.controller.driver_api.dto.CreateSessionResponse;
import com.nocmok.orp.api.controller.driver_api.dto.RoadSegment;
import com.nocmok.orp.api.service.session_management.SessionManagementService;
import com.nocmok.orp.api.service.session_management.dto.SessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller()
@RequestMapping("/driver_api/v0")
public class DriverApi {

    private SessionManagementService sessionManagementService;

    @Autowired
    public DriverApi(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    @PostMapping("/create_session")
    public @ResponseBody CreateSessionResponse createSession(@RequestBody CreateSessionRequest createSessionRequest) {
        var session = sessionManagementService.createSession(SessionDto.builder()
                .initialCapacity(createSessionRequest.getCapacity())
                .initialLatitude(createSessionRequest.getCoordinates().getLatitude())
                .initialLongitude(createSessionRequest.getCoordinates().getLongitude())
                .sourceId(createSessionRequest.getRoad().getSourceId())
                .targetId(createSessionRequest.getRoad().getTargetId())
                .createdAt(createSessionRequest.getCreatedAt())
                .build());

        return CreateSessionResponse.builder()
                .sessionId(session.getSessionId())
                .capacity(session.getInitialCapacity())
                .coordinates(new Coordinates(session.getInitialLatitude(), session.getInitialLongitude()))
                .road(new RoadSegment(session.getSourceId(), session.getTargetId()))
                .createdAt(session.getCreatedAt())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public @ResponseBody String unknownExceptionHandler(Exception exception) {
        return exception.getMessage();
    }
}
