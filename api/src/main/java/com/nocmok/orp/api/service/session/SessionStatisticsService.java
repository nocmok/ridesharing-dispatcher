package com.nocmok.orp.api.service.session;

import com.nocmok.orp.api.service.session.dto.SessionStatistics;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;

import java.util.Optional;

public interface SessionStatisticsService {

    Optional<SessionStatistics> getTotalSessionStatistics(String sessionId);

    Optional<SessionStatistics> getSessionStatisticsByStatus(String sessionId, SessionStatus status);
}
