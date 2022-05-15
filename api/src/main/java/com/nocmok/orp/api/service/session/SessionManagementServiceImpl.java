package com.nocmok.orp.api.service.session;

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
import com.nocmok.orp.state_keeper.api.DefaultVehicle;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class SessionManagementServiceImpl implements SessionManagementService {

    private SpatialGraphObjectsStorage graphObjectsStorage;
    private StateKeeper<?> stateKeeper;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private RouteCacheStorage routeCacheStorage;
    private SessionStorage sessionStorage;
    private OrderAssignmentStorage orderAssignmentStorage;

    @Autowired
    public SessionManagementServiceImpl(SpatialGraphObjectsStorage graphObjectsStorage, StateKeeper<?> stateKeeper,
                                        KafkaTemplate<String, Object> kafkaTemplate, RouteCacheStorage routeCacheStorage,
                                        SessionStorage sessionStorage, OrderAssignmentStorage orderAssignmentStorage) {
        this.graphObjectsStorage = graphObjectsStorage;
        this.stateKeeper = stateKeeper;
        this.kafkaTemplate = kafkaTemplate;
        this.routeCacheStorage = routeCacheStorage;
        this.sessionStorage = sessionStorage;
        this.orderAssignmentStorage = orderAssignmentStorage;
    }

    @Override public SessionDto createSession(SessionDto sessionDto) {
        // TODO делать транзакционно

        var vehicle = stateKeeper.createVehicle(DefaultVehicle.builder()
                .capacity(sessionDto.getInitialCapacity())
                .residualCapacity(sessionDto.getInitialCapacity())
                .status(VehicleStatus.PENDING)
                .build());

        sessionDto.setSessionId(vehicle.getId());

        graphObjectsStorage.updateObject(new ObjectUpdater(
                sessionDto.getSessionId(),
                sessionDto.getSourceId(),
                sessionDto.getTargetId(),
                sessionDto.getInitialLatitude(),
                sessionDto.getInitialLongitude()
        ));

        return sessionDto;
    }

    @Override public void stopSession(String sessionId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public SessionInfo getActiveSessionInfo(String sessionId) {
        var sessions = stateKeeper.getVehiclesByIds(List.of(sessionId));
        if (sessions.isEmpty()) {
            throw new RuntimeException("cannot found active sessions with id " + sessionId);
        }

        var session = sessions.get(0);
        var route = routeCacheStorage.getRouteCacheBySessionId(sessionId);

        return SessionInfo.builder()
                .id(sessionId)
                .schedule(session.getSchedule().asList())
                .capacity(session.getCapacity())
                .status(session.getStatus())
                .residualCapacity(session.getResidualCapacity())
                .routeScheduled(route)
                .build();
    }

    @Override public List<String> getActiveSessionsIds() {
        return stateKeeper.getActiveVehiclesIds();
    }

    private OrderStatus mapInternalOrderStatusToKafkaMessageOrderStatus(RequestStatus orderStatus) {
        switch (orderStatus) {
            case SERVING:
                return OrderStatus.SERVING;
            case SERVED:
                return OrderStatus.SERVED;
            case DENIED:
                return OrderStatus.DENIED;
            case SERVING_DENIED:
                return OrderStatus.SERVING_DENIED;
            default:
                throw new IllegalArgumentException("unknown order status " + orderStatus);
        }
    }

    @Override public void updateOrderStatus(String sessionId, String orderId, RequestStatus updatedStatus) {
        kafkaTemplate.send(new ProducerRecord<>(
                "orp.input",
                null,
                sessionId,
                UpdateOrderStatusMessage.builder()
                        .sessionId(sessionId)
                        .orderId(orderId)
                        .updatedStatus(mapInternalOrderStatusToKafkaMessageOrderStatus(updatedStatus))
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
}