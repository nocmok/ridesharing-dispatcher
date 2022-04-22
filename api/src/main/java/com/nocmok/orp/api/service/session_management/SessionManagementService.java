package com.nocmok.orp.api.service.session_management;

import com.nocmok.orp.api.service.session_management.dto.RequestStatus;
import com.nocmok.orp.api.service.session_management.dto.SessionDto;
import com.nocmok.orp.api.service.session_management.dto.SessionInfo;

import java.util.List;

public interface SessionManagementService {

    SessionDto createSession(SessionDto sessionDto);

    void stopSession(String sessionId);

    SessionInfo getSessionInfo(String sessionId);

    List<SessionInfo> getActiveSessions();

    List<String> getActiveSessionsIds();

    void updateOrderStatus(String sessionId, String orderId, RequestStatus updatedStatus);
}
