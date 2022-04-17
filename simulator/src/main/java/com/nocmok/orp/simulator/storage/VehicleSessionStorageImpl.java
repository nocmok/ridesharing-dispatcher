package com.nocmok.orp.simulator.storage;

import com.nocmok.orp.simulator.storage.dto.VehicleSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class VehicleSessionStorageImpl implements VehicleSessionStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public VehicleSessionStorageImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private VehicleSession mapResultSetToVehicleSession(ResultSet rs, int rowNum) throws SQLException {
        return VehicleSession.builder()
                .sessionId(Objects.toString(rs.getLong("session_id")))
                .residualCapacity((int) rs.getLong("residual_capacity"))
                .status(Objects.toString(rs.getObject("status")))
                .completedAt(Optional.ofNullable(rs.getTimestamp("completed_at")).map(Timestamp::toInstant).orElse(null))
                .createdAt(Optional.ofNullable(rs.getTimestamp("created_at")).map(Timestamp::toInstant).orElse(null))
                .totalCapacity((int) rs.getLong("total_capacity"))
                .build();
    }

    @Override public List<VehicleSession> readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(Instant timestamp) {
        var params = new HashMap<String, Object>();
        params.put("timestamp", Timestamp.from(timestamp));
        var sessions =
                jdbcTemplate.query("select * from vehicle_session where completed_at is null and created_at > :timestamp order by created_at asc", params,
                        this::mapResultSetToVehicleSession);
        return sessions;
    }
}
