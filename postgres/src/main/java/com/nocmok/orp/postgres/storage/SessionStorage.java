package com.nocmok.orp.postgres.storage;

import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.postgres.storage.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SessionStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final SessionIdSequence sessionIdSequence;

    @Autowired
    public SessionStorage(NamedParameterJdbcTemplate jdbcTemplate,
                          TransactionTemplate transactionTemplate, SessionIdSequence sessionIdSequence) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.sessionIdSequence = sessionIdSequence;
    }

    private Session parseVehicleFromResultSet(ResultSet rs, int nRow) throws SQLException {
        return Session.builder()
                .sessionId(rs.getLong("session_id"))
                .totalCapacity(rs.getLong("total_capacity"))
                .residualCapacity(rs.getLong("residual_capacity"))
                .scheduleJson(rs.getString("schedule_json"))
                .sessionStatus(Optional.ofNullable(rs.getString("status")).map(SessionStatus::valueOf).orElse(null))
                .build();
    }

    public List<Session> getSessionsByIdsOrderedByCreationTime(List<Long> ids, boolean ascending) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var sessions = jdbcTemplate.query(
                " select t1.session_id, t1.total_capacity, t1.residual_capacity, t1.schedule_json, t1.status " +
                        " from " +
                        " vehicle_session as t1 " +
                        " join " +
                        " (" +
                        "   select session_id, min(updated_at) as updated_at " +
                        "   from session_status_log " +
                        "   where session_id in (:ids) " +
                        "   group by session_id " +
                        " ) as t2 " +
                        " on t1.session_id = t2.session_id " +
                        " order by t2.updated_at " + (ascending ? "asc" : "desc"),
                params,
                this::parseVehicleFromResultSet);
        return sessions;
    }

    public List<Session> getSessionsByIds(List<Long> ids) {
        return getSessionsByIdsInternal(ids, false);
    }

    private List<Session> getSessionsByIdsInternal(List<Long> ids, boolean forUpdate) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var sessions = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json," +
                        " status " +
                        " from vehicle_session " +
                        " where session_id in (:ids) " +
                        (forUpdate ? " for update " : ""),
                params,
                this::parseVehicleFromResultSet);
        return sessions;
    }

    public List<Session> getActiveSessionsByIds(List<Long> ids) {
        return transactionTemplate.execute(status -> getSessionsByIdsInternal(getActiveSessionsIds(ids), false));
    }

    public Map<Long, SessionStatus> getSessionStatuses(List<Long> sessionIds) {
        var params = new HashMap<String, Object>();

        params.put("sessionIds", sessionIds);

        var result = jdbcTemplate.query(
                        " select t2.session_id, t2.status " +
                                " from " +
                                " (select session_id, max(updated_at) as updated_at " +
                                " from session_status_log " +
                                " where session_id in (:sessionIds) " +
                                " group by session_id) as t1 join session_status_log as t2 " +
                                " on t1.session_id = t2.session_id " +
                                " and t1.updated_at = t2.updated_at ", params,
                        (rs, rowNum) -> Map.entry(rs.getLong("session_id"), SessionStatus.valueOf(rs.getString("status"))))
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return result;
    }

    public List<Session> getActiveSessionsByIdsForUpdate(List<Long> ids) {
        return transactionTemplate.execute(status -> getSessionsByIdsInternal(getActiveSessionsIds(ids), true));
    }

    public void updateSession(Session session) {
        var params = new HashMap<String, Object>();
        params.put("totalCapacity", session.getTotalCapacity());
        params.put("residualCapacity", session.getResidualCapacity());
        params.put("scheduleJson", session.getScheduleJson());
        params.put("sessionId", session.getSessionId());
        jdbcTemplate.update(" update vehicle_session " +
                " set " +
                " total_capacity = coalesce(:totalCapacity, total_capacity), " +
                " residual_capacity = coalesce(:residualCapacity, residual_capacity), " +
                " schedule_json = coalesce(:scheduleJson, schedule_json) " +
                " where session_id = :sessionId ", params);
    }

    @Transactional
    public void updateSessionStatus(Long sessionId, SessionStatus updatedStatus) {
        updateSessionStatusInternal(sessionId, updatedStatus);
        appendToSessionStatusLog(sessionId, updatedStatus);
    }

    private void updateSessionStatusInternal(Long sessionId, SessionStatus updatedStatus) {
        var params = new HashMap<String, Object>();
        params.put("status", Optional.ofNullable(updatedStatus).map(Enum::name).orElse(null));
        params.put("sessionId", sessionId);
        jdbcTemplate.update(" update vehicle_session set status = :status::vehicle_status where session_id = :sessionId ", params);
    }

    private void appendToSessionStatusLog(Long sessionId, SessionStatus updatedStatus) {
        var params = new HashMap<String, Object>();
        params.put("sessionId", sessionId);
        params.put("updatedStatus", updatedStatus.name());
        params.put("updatedAt", Timestamp.from(Instant.now()));
        jdbcTemplate.update(" insert into " +
                " session_status_log(session_id, status, updated_at) " +
                " values(:sessionId, :updatedStatus::vehicle_status, :updatedAt) ", params);
    }

    public List<Long> getActiveSessionsIds() {
        return getActiveSessionsIds(Collections.emptyList());
    }

    public List<Long> getActiveSessionsIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        return jdbcTemplate.query(" select t1.session_id from " +
                        " (select session_id, max(updated_at) as updated_at from session_status_log " +
                        " where session_id in (:ids) " +
                        " group by session_id) as t1 " +
                        " join session_status_log as t2 " +
                        " on t1.session_id = t2.session_id " +
                        " and t1.updated_at = t2.updated_at " +
                        " where t2.status not in ('FROZEN', 'CLOSED') ",
                params,
                (rs, rowNum) -> rs.getLong("session_id"));
    }

    private Session insertSession(Session session) {
        session.setSessionId(sessionIdSequence.nextId());

        var params = new HashMap<String, Object>();

        params.put("sessionId", session.getSessionId());
        params.put("totalCapacity", session.getTotalCapacity());
        params.put("residualCapacity", session.getResidualCapacity());
        params.put("scheduleJson", session.getScheduleJson());
        params.put("status", Optional.ofNullable(session.getSessionStatus()).map(Enum::name).orElse(null));

        int rowsAffected =
                jdbcTemplate.update("insert into vehicle_session (session_id, total_capacity, residual_capacity, schedule_json, status) " +
                        "values(:sessionId, :totalCapacity, :residualCapacity, :scheduleJson, :status::vehicle_status)", params);

        if (rowsAffected != 1) {
            throw new RuntimeException("failed to insert session");
        }

        return session;
    }

    public Session createSession(Session session, SessionStatus initialStatus) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            insertSession(session);
            updateSessionStatus(session.getSessionId(), initialStatus);
        });
        return session;
    }

    public List<Long> getSessionsCreatedAfterTimestampIds(Instant timestamp) {
        return jdbcTemplate.query(" select session_id, min(updated_at) as updated_at " +
                        " from session_status_log " +
                        " group by session_id " +
                        " having min(updated_at) > :timestamp " +
                        " order by updated_at ",
                Map.of("timestamp", Timestamp.from(timestamp)), (rs, rn) -> rs.getLong("session_id"));
    }

    private Session.StatusLogEntry parseStatusLogFromResultSet(ResultSet rs, int rNum) throws SQLException {
        return new Session.StatusLogEntry(SessionStatus.valueOf(rs.getString("status")), rs.getTimestamp("updated_at").toInstant());
    }

    /**
     * @param ascendingOrder если true, то 0 страницы содержит самые старые записи, если false, то 0 страница содержит самые новые записи
     */
    public Map<Long, List<Session.StatusLogEntry>> getSessionsStatusLog(List<Long> ids, int pageNumber, int entriesPerPage, boolean ascendingOrder) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        params.put("fromInclusive", pageNumber * entriesPerPage);
        params.put("toExclusive", pageNumber * entriesPerPage + entriesPerPage);
        var statusLogEntries = jdbcTemplate.query(
                " select session_id, status, updated_at " +
                        " from (" +
                        "    select session_id, status, updated_at, " +
                        "    ((row_number() over (partition by session_id order by updated_at " + (ascendingOrder ? "asc" : "desc") + ")) - 1) as rn " +
                        "    from session_status_log " +
                        "    where session_id in (:ids) " +
                        " ) as t " +
                        " where rn >= :fromInclusive and rn < :toExclusive ",
                params,
                (rs, rn) -> Map.entry(rs.getLong("session_id"), parseStatusLogFromResultSet(rs, rn)));

        return statusLogEntries.stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public List<Session.StatusLogEntry> getSessionStatusLog(Long id, int pageNumber, int entriesPerPage, boolean ascendingOrder) {
        return getSessionsStatusLog(List.of(id), pageNumber, entriesPerPage, ascendingOrder).getOrDefault(id, Collections.emptyList());
    }

    private <T> List<T> queryFilter(String tableName, Filter filter, RowMapper<T> mapper) {
        var sql = filter.applyPaging(" select * from " + tableName + filter.getWhereString().map(" where "::concat).orElse(""));
        return jdbcTemplate.query(sql, filter.getParamsMap(), mapper);
    }

    public List<Session> getSessions(Filter filter) {
        return queryFilter("vehicle_session", filter, this::parseVehicleFromResultSet);
    }
}
