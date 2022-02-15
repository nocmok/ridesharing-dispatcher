package com.nocmok.orp.telemetry.storage;

import com.nocmok.orp.telemetry.storage.dto.VehicleTelemetryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class TelemetryStoragePostgres implements TelemetryStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TelemetryStoragePostgres(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    private VehicleTelemetryRecord mapResultSetToVehicleTelemetry(ResultSet resultSet, int nRow) throws SQLException {
        return new VehicleTelemetryRecord(
                resultSet.getString("session_id"),
                resultSet.getDouble("latitude"),
                resultSet.getDouble("longitude"),
                resultSet.getDouble("accuracy"),
                Optional.ofNullable(resultSet.getObject("recorded_at", Timestamp.class))
                        .map(Timestamp::toInstant)
                        .orElse(null)
        );
    }


    @Override public void appendTelemetryBatch(List<VehicleTelemetryRecord> telemetryBatch) {
        if (telemetryBatch.isEmpty()) {
            return;
        }
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " insert into telemetry (session_id, latitude, longitude, accuracy, recorded_at) values(?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var telemetry = telemetryBatch.get(i);
                        ps.setLong(1, Long.parseLong(telemetry.getSessionId()));
                        ps.setDouble(2, telemetry.getLatitude());
                        ps.setDouble(3, telemetry.getLongitude());
                        ps.setDouble(4, telemetry.getAccuracy());
                        ps.setObject(5, telemetry.getRecordedAt() != null ? Timestamp.from(telemetry.getRecordedAt()) : null, Types.TIMESTAMP);
                    }

                    @Override public int getBatchSize() {
                        return telemetryBatch.size();
                    }
                });
    }

    @Override public List<VehicleTelemetryRecord> getLatestRecordsForEachVehicleAfterTimestamp(Instant timestamp) {
        var params = new HashMap<String, Object>();
        params.put("timestamp", Timestamp.from(timestamp));
        return jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " latitude," +
                        " longitude," +
                        " accuracy," +
                        " recorded_at " +
                        " from telemetry " +
                        " where recorded_at >= :timestamp " +
                        " order by recorded_at asc ",
                params,
                this::mapResultSetToVehicleTelemetry
        );
    }
}
