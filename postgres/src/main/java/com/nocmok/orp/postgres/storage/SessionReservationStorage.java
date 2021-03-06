package com.nocmok.orp.postgres.storage;

import com.nocmok.orp.postgres.storage.dto.SessionReservationEntry;
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
import java.util.stream.Collectors;

@Repository
public class SessionReservationStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SessionReservationStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private SessionReservationEntry parseVehicleReservationEntryFromResultSet(ResultSet rs, int nRow) throws SQLException {
        return SessionReservationEntry.builder()
                .reservationId(Objects.toString(rs.getLong("reservation_id")))
                .vehicleId(Objects.toString(rs.getLong("session_id")))
                .requestId(Objects.toString(rs.getLong("request_id")))
                .createdAt(Optional.ofNullable(rs.getObject("created_at", Timestamp.class)).map(Timestamp::toInstant).orElse(null))
                .expiredAt(Optional.ofNullable(rs.getObject("expired_at", Timestamp.class)).map(Timestamp::toInstant).orElse(null))
                .build();
    }

    public List<SessionReservationEntry> getNotExpiredReservationsByVehicleIdsForUpdate(List<String> vehicleIds) {
        var params = new HashMap<String, Object>();
        params.put("ids", vehicleIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
        return jdbcTemplate.query(
                " select " +
                        " reservation_id, " +
                        " session_id, " +
                        " request_id, " +
                        " created_at, " +
                        " expired_at  " +
                        " from vehicle_reservation " +
                        " where expired_at is null and session_id in (:ids) " +
                        " for update ", params, this::parseVehicleReservationEntryFromResultSet);
    }

    public void insertVehicleReservationBatch(List<SessionReservationEntry> batch) {
        var batchArray = new ArrayList<>(batch);
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " insert into vehicle_reservation (reservation_id, session_id, request_id, created_at, expired_at) values(?,?,?,?,?) ",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var reservation = batchArray.get(i);
                        ps.setLong(1, Long.parseLong(reservation.getReservationId()));
                        ps.setLong(2, Long.parseLong(reservation.getVehicleId()));
                        ps.setLong(3, Long.parseLong(reservation.getRequestId()));
                        ps.setObject(4, Optional.ofNullable(reservation.getCreatedAt()).map(Timestamp::from).orElse(null));
                        ps.setObject(5, Optional.ofNullable(reservation.getExpiredAt()).map(Timestamp::from).orElse(null));
                    }

                    @Override public int getBatchSize() {
                        return batchArray.size();
                    }
                });
    }

    public void invalidateExpiredReservations(Integer timeToLiveSeconds) {
        var params = new HashMap<String, Object>();
        params.put("ttl", timeToLiveSeconds);
        jdbcTemplate
                .update(" update vehicle_reservation " +
                                " set expired_at = now()" +
                                " where extract(epoch from (now() - created_at)) >= :ttl ",
                        params
                );
    }

    public Optional<SessionReservationEntry> getReservationById(String id) {
        var params = new HashMap<String, Object>();
        params.put("reservationId", Long.parseLong(id));
        return jdbcTemplate.query(" select " +
                                " reservation_id, " +
                                " session_id, " +
                                " request_id, " +
                                " created_at, " +
                                " expired_at  " +
                                " from vehicle_reservation " +
                                " where reservation_id = :reservationId ",
                        params, this::parseVehicleReservationEntryFromResultSet)
                .stream()
                .findFirst();
    }

    public void updateReservation(SessionReservationEntry entry) {
        var params = new HashMap<String, Object>();
        params.put("sessionId", Long.parseLong(entry.getVehicleId()));
        params.put("requestId", Long.parseLong(entry.getRequestId()));
        params.put("createdAt", Optional.ofNullable(entry.getCreatedAt()).map(Timestamp::from).orElse(null));
        params.put("expiredAt", Optional.ofNullable(entry.getExpiredAt()).map(Timestamp::from).orElse(null));
        params.put("reservationId", Long.parseLong(entry.getReservationId()));
        jdbcTemplate
                .update(" update vehicle_reservation " +
                                " set " +
                                " session_id = :sessionId, " +
                                " request_id = :requestId, " +
                                " created_at = :createdAt, " +
                                " expired_at = :expiredAt  " +
                                " where reservation_id = :reservationId ",
                        params
                );
    }
}
