package com.nocmok.orp.api.controller.session_api;

import com.nocmok.orp.api.controller.common_dto.SessionStatusLogEntry;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionExpendituresRequest;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionExpendituresResponse;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionOrdersRequest;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionOrdersResponse;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionStatusLogRequest;
import com.nocmok.orp.api.controller.session_api.dto.GetSessionStatusLogResponse;
import com.nocmok.orp.api.controller.session_api.dto.SessionExpenditure;
import com.nocmok.orp.api.controller.session_api.dto.SessionOrderAssignment;
import com.nocmok.orp.api.service.session.SessionManagementService;
import com.nocmok.orp.api.service.session.SessionStatisticsService;
import com.nocmok.orp.api.service.session.dto.SessionStatistics;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/session_api/v0")
public class SessionApiController {

    private SessionManagementService sessionManagementService;
    private SessionStatisticsService sessionStatisticsService;

    @Autowired
    public SessionApiController(SessionManagementService sessionManagementService,
                                SessionStatisticsService sessionStatisticsService) {
        this.sessionManagementService = sessionManagementService;
        this.sessionStatisticsService = sessionStatisticsService;
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

    @PostMapping("/statistics/orders")
    public @ResponseBody GetSessionOrdersResponse getSessionOrders(@RequestBody GetSessionOrdersRequest request) {
        var orderAssignments =
                sessionManagementService.getAssignedOrders(request.getSessionId(), request.getPage(), request.getEntriesPerPage(), request.getAscending());
        return GetSessionOrdersResponse.builder()
                .sessionId(request.getSessionId())
                .orders(orderAssignments.stream().map(orderAssignment -> SessionOrderAssignment.builder()
                        .orderId(Objects.toString(orderAssignment.getOrderId()))
                        .sessionId(Objects.toString(orderAssignment.getSessionId()))
                        .assignedAt(orderAssignment.getAssignedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private SessionExpenditure mapSessionStatisticsToSessionExpenditure(SessionStatistics sessionStatistics) {
        return SessionExpenditure.builder()
                .distanceTravelled(sessionStatistics.getDistanceTravelled())
                .build();
    }

    @PostMapping("/statistics/expenditures")
    public @ResponseBody GetSessionExpendituresResponse getSessionExpenditures(@RequestBody GetSessionExpendituresRequest request) {
        return GetSessionExpendituresResponse.builder()
                .expenditure("total",sessionStatisticsService.getTotalSessionStatistics(request.getSessionId()).map(this::mapSessionStatisticsToSessionExpenditure).orElse(null))
                .expenditure(SessionStatus.PENDING.name(), sessionStatisticsService.getSessionStatisticsByStatus(request.getSessionId(), SessionStatus.PENDING).map(this::mapSessionStatisticsToSessionExpenditure).orElse(null))
                .expenditure(SessionStatus.SERVING.name(), sessionStatisticsService.getSessionStatisticsByStatus(request.getSessionId(), SessionStatus.SERVING).map(this::mapSessionStatisticsToSessionExpenditure).orElse(null))
                .expenditure(SessionStatus.FROZEN.name(), sessionStatisticsService.getSessionStatisticsByStatus(request.getSessionId(), SessionStatus.FROZEN).map(this::mapSessionStatisticsToSessionExpenditure).orElse(null))
                .build();
    }
}
