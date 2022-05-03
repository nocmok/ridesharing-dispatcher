package com.nocmok.orp.simulator.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.simulator.storage.dto.VehicleSession;
import com.nocmok.orp.solver.api.ReadOnlySchedule;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class VehicleSessionStorageImpl implements VehicleSessionStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public VehicleSessionStorageImpl(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private Schedule parseScheduleFromJson(String json) {
        try {
            return objectMapper.readValue(json, ReadOnlySchedule.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VehicleSession mapResultSetToVehicleSession(ResultSet rs, int rowNum) throws SQLException {
        return VehicleSession.builder()
                .sessionId(Objects.toString(rs.getLong("session_id")))
                .residualCapacity((int) rs.getLong("residual_capacity"))
                .status(VehicleStatus.valueOf(rs.getString("status")))
                .completedAt(Optional.ofNullable(rs.getTimestamp("completed_at")).map(Timestamp::toInstant).orElse(null))
                .createdAt(Optional.ofNullable(rs.getTimestamp("created_at")).map(Timestamp::toInstant).orElse(null))
                .totalCapacity((int) rs.getLong("total_capacity"))
                .schedule(parseScheduleFromJson(rs.getString("schedule_json")).asList())
                .build();
    }

    @Override public Optional<VehicleSession> getSessionById(String sessionId) {
        var sessions = jdbcTemplate.query(
                "select session_id, residual_capacity, status, completed_at, created_at, total_capacity, schedule_json " +
                        " from vehicle_session where session_id = :sessionId", Map.of("sessionId", Long.parseLong(sessionId)),
                this::mapResultSetToVehicleSession);
        return CollectionUtils.isEmpty(sessions) ? Optional.empty() : Optional.of(sessions.get(0));
    }

    @Override public List<VehicleSession> readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(Instant timestamp) {
        var params = new HashMap<String, Object>();
        params.put("timestamp", Timestamp.from(timestamp));
        var sessions =
                jdbcTemplate.query(
                        " select session_id, residual_capacity, status, completed_at, created_at, total_capacity, schedule_json " +
                                " from vehicle_session where completed_at is null and created_at > :timestamp order by created_at asc ",
                        params,
                        this::mapResultSetToVehicleSession);
        return sessions;
    }
}
