package com.nocmok.orp.orp_solver.storage.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ServiceRequestOutboxStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceRequestOutboxStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertOne(ServiceRequestOutboxEntry serviceRequestOutboxEntry) {
        var params = new HashMap<String, Object>();
        params.put("vehicleId", Long.parseLong(serviceRequestOutboxEntry.getVehicleId()));
        params.put("requestId", Long.parseLong(serviceRequestOutboxEntry.getRequestId()));
        params.put("reservationId", Long.parseLong(serviceRequestOutboxEntry.getReservationId()));
        jdbcTemplate.update(" insert into service_request_outbox(session_id, request_id, reservation_id, sent_at) " +
                " values(:vehicleId, :requestId, :reservationId, null) ", params);
    }

    private ServiceRequestOutboxEntry mapResultSetToServiceRequestOutboxEntry(ResultSet rs, int nRow) throws SQLException {
        return ServiceRequestOutboxEntry.builder()
                .vehicleId(Objects.toString(rs.getLong("session_id")))
                .requestId(Objects.toString(rs.getLong("request_id")))
                .reservationId(Objects.toString(rs.getLong("reservation_id")))
                .sentAt(Optional.ofNullable(rs.getObject("sent_at", Timestamp.class)).map(Timestamp::toInstant).orElse(null))
                .build();
    }

    public List<ServiceRequestOutboxEntry> getUnsentEntriesBatchForUpdateSkipLocked(Integer maxBatchSize) {
        var params = new HashMap<String, Object>();
        params.put("maxBatchSize", maxBatchSize);
        return jdbcTemplate.query(" select " +
                        " session_id, " +
                        " request_id, " +
                        " reservation_id, " +
                        " sent_at " +
                        " from service_request_outbox " +
                        " where sent_at is null" +
                        " limit :maxBatchSize" +
                        " for update skip locked ", params,
                this::mapResultSetToServiceRequestOutboxEntry
        );
    }

    public void updateEntriesBatch(List<ServiceRequestOutboxEntry> batch) {
        var batchArray = new ArrayList<>(batch);
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " update service_request_outbox " +
                        " set " +
                        " session_id = coalesce(?, session_id)," +
                        " reservation_id = coalesce(?, session_id), " +
                        " sent_at = ? " +
                        " where request_id = ?",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var entry = batchArray.get(i);
                        ps.setLong(1, Long.parseLong(entry.getVehicleId()));
                        ps.setLong(2, Long.parseLong(entry.getReservationId()));
                        ps.setObject(3, Optional.ofNullable(entry.getSentAt()).map(Timestamp::from).orElse(null));
                        ps.setLong(4, Long.parseLong(entry.getRequestId()));
                    }

                    @Override public int getBatchSize() {
                        return batchArray.size();
                    }
                }
        );
    }
}
