package com.nocmok.orp.api.service.session;

import com.nocmok.orp.api.service.session.dto.RequestStatus;
import com.nocmok.orp.api.service.session.dto.SessionDto;
import com.nocmok.orp.api.service.session.dto.SessionInfo;
import com.nocmok.orp.kafka.orp_input.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.OrderAssignment;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.filter.Filter;
import com.nocmok.orp.solver.api.RouteNode;

import java.util.List;

public interface SessionManagementService {

    SessionDto createSession(Long capacity, Double initialLatitude, Double initialLongitude, String sourceId, String targetId);

    void stopSession(String sessionId);

    SessionInfo getActiveSessionInfo(String sessionId);

    List<String> getActiveSessionsIds();

    void updateOrderStatus(String sessionId, String orderId, OrderStatus updatedStatus);

    List<Session.StatusLogEntry> getSessionStatusLog(String sessionId, int pageNumber, int pageSize, boolean ascendingOrder);

    List<OrderAssignment> getAssignedOrders(String sessionId, int pageNumber, int pageSize, boolean ascendingOrder);

    List<SessionDto> getSessionsByFilter(Filter filter);

    List<RouteNode> getLatestSessionRoute(String sessionId);
}
