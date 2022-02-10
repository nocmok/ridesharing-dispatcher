package com.nocmok.orp.telemetry.storage;

import com.nocmok.orp.telemetry.dto.VehicleTelemetry;
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
import java.util.List;

@Component
public class TelemetryStoragePostgres implements TelemetryStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TelemetryStoragePostgres(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    private VehicleTelemetry mapResultSetToVehicleTelemetry(ResultSet resultSet, int nRow) throws SQLException {
        return new VehicleTelemetry(
                resultSet.getString("session_id"),
                resultSet.getDouble("lat"),
                resultSet.getDouble("lon"),
                resultSet.getDouble("accuracy"),
                resultSet.getObject("recorded_at", Timestamp.class).toInstant()
        );
    }

    @Override public List<VehicleTelemetry> getTelemetryBatch(int batchSize) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override public void appendTelemetryBatch(List<VehicleTelemetry> telemetryBatch) {
        if (telemetryBatch.isEmpty()) {
            return;
        }
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " insert into telemetry (session_id, lat, lon, accuracy, recorded_at) values(?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var telemetry = telemetryBatch.get(i);
                        ps.setLong(1, Long.parseLong(telemetry.getSessionId()));
                        ps.setDouble(2, telemetry.getLat());
                        ps.setDouble(3, telemetry.getLon());
                        ps.setDouble(4, telemetry.getAccuracy());
                        ps.setObject(5, Timestamp.from(telemetry.getRecordedAt()), Types.TIMESTAMP);
                    }

                    @Override public int getBatchSize() {
                        return telemetryBatch.size();
                    }
                });
    }
}
