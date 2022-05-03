package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.solver.api.EmptySchedule;
import com.nocmok.orp.solver.api.ReadOnlySchedule;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class VehicleStateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final SessionIdSequence sessionIdSequence;
    private final ObjectMapper objectMapper;

    @Autowired
    public VehicleStateRepository(NamedParameterJdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager,
                                  TransactionTemplate transactionTemplate, SessionIdSequence sessionIdSequence,
                                  ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = transactionManager;
        this.transactionTemplate = transactionTemplate;
        this.sessionIdSequence = sessionIdSequence;
        this.objectMapper = objectMapper;
    }

    public Schedule tryParseDefaultSchedule(String json) throws Exception {
        return objectMapper.readValue(json, ReadOnlySchedule.class);
    }

    private Schedule jsonToSchedule(String json) {
        try {
            return objectMapper.readValue(json, Schedule.class);
        } catch (Exception e) {
            try {
                return tryParseDefaultSchedule(json);
            } catch (Exception suppress) {
                var exceptionToThrow = new RuntimeException(e);
                exceptionToThrow.addSuppressed(suppress);
                throw exceptionToThrow;
            }
        }
    }

    private String scheduleToJson(Schedule schedule) {
        try {
            return objectMapper.writeValueAsString(schedule);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VehicleDto parseVehicleFromResultSet(ResultSet rs, int nRow) throws SQLException {
        return VehicleDto.builder()
                .id(Objects.toString(rs.getLong("session_id")))
                .status(VehicleStatus.valueOf(rs.getString("status")))
                .capacity(rs.getInt("total_capacity"))
                .residualCapacity(rs.getInt("residual_capacity"))
                .schedule(jsonToSchedule(rs.getString("schedule_json")))
                .build();
    }

    public List<VehicleDto> getVehiclesByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var vehicles = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json " +
                        " from vehicle_session " +
                        " where session_id in (:ids) and completed_at is null",
                params,
                this::parseVehicleFromResultSet);
        return vehicles;
    }

    public List<VehicleDto> getVehiclesByIdsForUpdate(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var vehicles = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json " +
                        " from vehicle_session " +
                        " where session_id in (:ids) and completed_at is null " +
                        " for update ",
                params,
                this::parseVehicleFromResultSet);
        return vehicles;
    }

    public void updateVehiclesBatch(List<? extends VehicleState> vehicles) {
        if (vehicles.isEmpty()) {
            return;
        }
        var vehiclesList = new ArrayList<>(vehicles);
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " update vehicle_session " +
                        " set " +
                        " status = coalesce(cast(? as vehicle_status), status), " +
                        " total_capacity = coalesce(?, total_capacity), " +
                        " residual_capacity = coalesce(?, residual_capacity), " +
                        " schedule_json = coalesce(?, schedule_json) " +
                        " where session_id = ? ",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var vehicle = vehiclesList.get(i);
                        ps.setString(1, Optional.ofNullable(vehicle.getStatus()).map(Objects::toString).orElse(null));
                        ps.setObject(2, vehicle.getCapacity(), Types.BIGINT);
                        ps.setObject(3, vehicle.getResidualCapacity(), Types.BIGINT);
                        ps.setString(4, Optional.ofNullable(vehicle.getSchedule()).map(VehicleStateRepository.this::scheduleToJson).orElse(null));
                        ps.setLong(5, Long.parseLong(vehicle.getId()));
                    }

                    @Override public int getBatchSize() {
                        return vehicles.size();
                    }
                });
    }

    public List<String> getActiveVehiclesIds() {
        return jdbcTemplate.query("select session_id from vehicle_session where completed_at is null",
                (rs, rowNum) -> Objects.toString(rs.getLong("session_id")));
    }

    public List<VehicleDto> getActiveVehicles() {
        var vehicles = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json " +
                        " from vehicle_session " +
                        " where completed_at is null",
                this::parseVehicleFromResultSet);
        return vehicles;
    }

    public VehicleState createVehicle(VehicleState vehicle) {
        vehicle.setId(Long.toString(sessionIdSequence.nextId()));
        vehicle.setSchedule(Objects.requireNonNullElse(vehicle.getSchedule(), new EmptySchedule()));

        var params = new HashMap<String, Object>();
        params.put("sessionId", Long.parseLong(vehicle.getId()));
        params.put("status", Objects.requireNonNullElse(vehicle.getStatus(), VehicleStatus.PENDING).name());
        params.put("totalCapacity", vehicle.getCapacity());
        params.put("residualCapacity", vehicle.getResidualCapacity());
        params.put("schedule", scheduleToJson(vehicle.getSchedule()));

        int rowsAffected =
                jdbcTemplate.update("insert into vehicle_session (session_id, created_at, status, total_capacity, residual_capacity, schedule_json) " +
                        "values(:sessionId, now(), cast(:status as vehicle_status), :totalCapacity, :residualCapacity, :schedule)", params);

        if (rowsAffected != 1) {
            throw new RuntimeException("failed to insert vehicle");
        }

        return vehicle;
    }


}
