package com.nocmok.orp.api.service.session_management;

import com.nocmok.orp.api.service.session_management.dto.RequestStatus;
import com.nocmok.orp.api.service.session_management.dto.SessionDto;
import com.nocmok.orp.api.service.session_management.dto.SessionInfo;
import com.nocmok.orp.postgres.storage.dto.Session;

import java.util.List;

public interface SessionManagementService {

    SessionDto createSession(SessionDto sessionDto);

    void stopSession(String sessionId);

    SessionInfo getActiveSessionInfo(String sessionId);

    List<String> getActiveSessionsIds();

    void updateOrderStatus(String sessionId, String orderId, RequestStatus updatedStatus);

    List<Session.StatusLogEntry> getSessionStatusLog(String sessionId, int pageNumber, int pageSize, boolean ascendingOrder);
}
