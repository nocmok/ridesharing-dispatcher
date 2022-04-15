package com.nocmok.orp.api.service.session_management;

import com.nocmok.orp.api.service.session_management.dto.SessionDto;
import com.nocmok.orp.api.service.session_management.dto.SessionInfo;

public interface SessionManagementService {

    SessionDto createSession(SessionDto sessionDto);

    void stopSession(String sessionId);

    SessionInfo getSessionInfo(String sessionId);
}
