package com.nocmok.orp.api.service.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.api.service.session.dto.RequestStatus;
import com.nocmok.orp.api.service.session.dto.SessionDto;
import com.nocmok.orp.api.service.session.dto.SessionInfo;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.kafka.orp_input.OrderStatus;
import com.nocmok.orp.kafka.orp_input.UpdateOrderStatusMessage;
import com.nocmok.orp.postgres.storage.OrderAssignmentStorage;
import com.nocmok.orp.postgres.storage.RouteCacheStorage;
import com.nocmok.orp.postgres.storage.SessionStorage;
import com.nocmok.orp.postgres.storage.dto.OrderAssignment;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.postgres.storage.filter.Filter;
import com.nocmok.orp.solver.api.EmptySchedule;
import com.nocmok.orp.solver.api.ReadOnlySchedule;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleEntry;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SessionManagementServiceImpl implements SessionManagementService {

    private SpatialGraphObjectsStorage graphObjectsStorage;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private RouteCacheStorage routeCacheStorage;
    private SessionStorage sessionStorage;
    private OrderAssignmentStorage orderAssignmentStorage;
    private ObjectMapper objectMapper;

    @Autowired
    public SessionManagementServiceImpl(SpatialGraphObjectsStorage graphObjectsStorage,
                                        KafkaTemplate<String, Object> kafkaTemplate, RouteCacheStorage routeCacheStorage,
                                        SessionStorage sessionStorage, OrderAssignmentStorage orderAssignmentStorage,
                                        ObjectMapper objectMapper) {
        this.graphObjectsStorage = graphObjectsStorage;
        this.kafkaTemplate = kafkaTemplate;
        this.routeCacheStorage = routeCacheStorage;
        this.sessionStorage = sessionStorage;
        this.orderAssignmentStorage = orderAssignmentStorage;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override public SessionDto createSession(Long capacity, Double initialLatitude, Double initialLongitude, String sourceId, String targetId) {
        Objects.requireNonNull(capacity);

        var session = sessionStorage.createSession(Session.builder()
                .totalCapacity(capacity)
                .residualCapacity(capacity)
                .scheduleJson(emptyScheduleJson())
                .build(), SessionStatus.PENDING);

        graphObjectsStorage.updateObject(new ObjectUpdater(
                Objects.toString(session.getSessionId()),
                sourceId,
                targetId,
                initialLatitude,
                initialLongitude
        ));

        return SessionDto.builder()
                .sessionId(Objects.toString(session.getSessionId()))
                .capacity(session.getTotalCapacity())
                .residualCapacity(session.getResidualCapacity())
                .schedule(Collections.emptyList())
                .build();
    }

    @Override public void stopSession(String sessionId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public SessionInfo getActiveSessionInfo(String sessionId) {
        var sessions = sessionStorage.getSessionsByIds(List.of(Long.parseLong(sessionId)));
        if (sessions.isEmpty()) {
            throw new RuntimeException("cannot found active sessions with id " + sessionId);
        }

        var session = sessions.get(0);
        var route = routeCacheStorage.getRouteCacheBySessionId(sessionId);

        return SessionInfo.builder()
                .id(sessionId)
                .schedule(parseDefaultScheduleFromJson(session.getScheduleJson()))
                .capacity(session.getTotalCapacity())
                .status(session.getSessionStatus())
                .residualCapacity(session.getResidualCapacity())
                .routeScheduled(route)
                .build();
    }

    @Override public List<String> getActiveSessionsIds() {
        return sessionStorage.getSessions(new Filter().oneOf(Session.Fields.terminatedAt, Arrays.asList(new Instant[]{null}))).stream()
                .map(Session::getSessionId)
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    @Override public void updateOrderStatus(String sessionId, String orderId, OrderStatus updatedStatus) {
        kafkaTemplate.send(new ProducerRecord<>(
                "orp.input",
                null,
                sessionId,
                UpdateOrderStatusMessage.builder()
                        .sessionId(sessionId)
                        .orderId(orderId)
                        .updatedStatus(updatedStatus)
                        .build(),
                List.of(new RecordHeader("__TypeId__", UpdateOrderStatusMessage.class.getName().getBytes(StandardCharsets.UTF_8)))
        ));
    }

    @Override public List<Session.StatusLogEntry> getSessionStatusLog(String sessionId, int pageNumber, int pageSize, boolean ascendingOrder) {
        return sessionStorage.getSessionStatusLog(Long.parseLong(sessionId), pageNumber, pageSize, ascendingOrder);
    }

    @Override public List<OrderAssignment> getAssignedOrders(String sessionId, int pageNumber, int pageSize, boolean ascendingOrder) {
        return orderAssignmentStorage.getSessionAssignments(Long.parseLong(sessionId), pageNumber, pageSize, ascendingOrder);
    }

    private List<ScheduleEntry> parseDefaultScheduleFromJson(String json) {
        try {
            return objectMapper.readValue(json, ReadOnlySchedule.class).asList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String emptyScheduleJson() {
        try {
            return objectMapper.writeValueAsString(new EmptySchedule());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public List<SessionDto> getSessionsByFilter(Filter filter) {
        return sessionStorage.getSessions(filter).stream().map(session -> SessionDto.builder()
                .sessionId(Objects.toString(session.getSessionId()))
                .status(session.getSessionStatus())
                .capacity(session.getTotalCapacity())
                .residualCapacity(session.getResidualCapacity())
                .schedule(parseDefaultScheduleFromJson(session.getScheduleJson()))
                .startedAt(session.getStartedAt())
                .terminatedAt(session.getTerminatedAt())
                .build()).collect(Collectors.toList());
    }

    @Override public List<RouteNode> getLatestSessionRoute(String sessionId) {
        return routeCacheStorage.getRouteCacheBySessionId(sessionId);
    }
}
