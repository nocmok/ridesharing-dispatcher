package com.nocmok.orp.api.controller.session_api;

import com.nocmok.orp.api.controller.common_dto.SessionStatusLogEntry;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionStatusLogRequest;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionStatusLogResponse;
import com.nocmok.orp.api.service.session_management.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/session_api/v0")
public class SessionApiController {

    private SessionManagementService sessionManagementService;

    @Autowired
    public SessionApiController(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    @PostMapping("/statistics/status_log")
    public @ResponseBody GetSessionStatusLogResponse getSessionStatusLog(@RequestBody GetSessionStatusLogRequest request) {
        var statusLog =
                sessionManagementService.getSessionStatusLog(request.getSessionId(), request.getPage(), request.getEntriesPerPage(), request.getAscending());
        return GetSessionStatusLogResponse.builder()
                .sessionId(request.getSessionId())
                .statusLog(statusLog.stream().map(statusLogEntry -> SessionStatusLogEntry.builder()
                                .status(statusLogEntry.getStatus().name())
                                .updatedAt(statusLogEntry.getTimestamp())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
