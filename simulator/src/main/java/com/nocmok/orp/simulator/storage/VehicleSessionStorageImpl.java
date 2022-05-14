package com.nocmok.orp.simulator.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.postgres.storage.SessionStorage;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.simulator.storage.dto.VehicleSession;
import com.nocmok.orp.solver.api.ReadOnlySchedule;
import com.nocmok.orp.solver.api.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Slf4j
public class VehicleSessionStorageImpl implements VehicleSessionStorage {

    private ObjectMapper objectMapper;
    private SessionStorage sessionStorage;

    @Autowired
    public VehicleSessionStorageImpl(ObjectMapper objectMapper,
                                     SessionStorage sessionStorage) {
        this.objectMapper = objectMapper;
        this.sessionStorage = sessionStorage;
    }

    private Schedule parseScheduleFromJson(String json) {
        try {
            return objectMapper.readValue(json, ReadOnlySchedule.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VehicleSession parseFromSessionAndStatusLog(Session session, List<Session.StatusLogEntry> statusLog) {
        if (statusLog.isEmpty()) {
            throw new IllegalArgumentException("status log should not be empty");
        }
        return VehicleSession.builder()
                .sessionId(Long.toString(session.getSessionId()))
                .createdAt(statusLog.get(0).getTimestamp())
                .totalCapacity(Optional.ofNullable(session.getTotalCapacity()).map(Long::intValue).orElse(null))
                .residualCapacity(Optional.ofNullable(session.getResidualCapacity()).map(Long::intValue).orElse(null))
                .schedule(parseScheduleFromJson(session.getScheduleJson()).asList())
                .status(statusLog.get(statusLog.size() - 1).getStatus())
                .build();
    }

    @Override public Optional<VehicleSession> getSessionById(String sessionId) {
        var session = sessionStorage.getSessionsByIds(List.of(Long.parseLong(sessionId))).stream().findAny();
        if (session.isEmpty()) {
            return Optional.empty();
        }

        var statusLog = getSessionsFirstAndLastStatusLogEntries(List.of(Long.parseLong(sessionId)))
                .getOrDefault(Long.parseLong(sessionId), Collections.emptyList());

        if (statusLog.isEmpty()) {
            log.warn("empty status log for session with id {}", sessionId);
            return Optional.empty();
        }
        return Optional.of(parseFromSessionAndStatusLog(session.get(), statusLog));
    }

    /**
     * Возвращает для каждой сессии лог статусов в котором содержится только первая и последняя запись полного лога
     */
    private Map<Long, List<Session.StatusLogEntry>> getSessionsFirstAndLastStatusLogEntries(List<Long> ids) {
        var statusLogsEarliestEntries = sessionStorage.getSessionsStatusLog(ids, 0, 1, false);
        var statusLogsLatestEntries = sessionStorage.getSessionsStatusLog(ids, 0, 1, true);
        var statusLogs = ids.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> Stream.concat(statusLogsEarliestEntries.get(id).stream(), statusLogsLatestEntries.get(id).stream())
                                .collect(Collectors.toList())));
        return statusLogs;
    }

    @Transactional
    @Override public List<VehicleSession> readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(Instant timestamp) {
        var activeSessionIds = sessionStorage.getActiveSessionsIds(sessionStorage.getSessionsCreatedAfterTimestampIds(timestamp));
        if (activeSessionIds.isEmpty()) {
            return Collections.emptyList();
        }
        var activeSessions = sessionStorage.getSessionsByIdsOrderedByCreationTime(activeSessionIds, true);
        var statusLogs = getSessionsFirstAndLastStatusLogEntries(activeSessionIds);
        return activeSessions.stream()
                .map(session -> parseFromSessionAndStatusLog(session, statusLogs.get(session.getSessionId())))
                .collect(Collectors.toList());
    }
}
