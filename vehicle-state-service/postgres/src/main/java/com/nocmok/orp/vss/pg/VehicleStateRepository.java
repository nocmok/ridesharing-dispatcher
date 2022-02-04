package com.nocmok.orp.vss.pg;

import com.nocmok.orp.core_api.VehicleStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class VehicleStateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final ScheduleJsonMapper scheduleJsonMapper = new ScheduleJsonMapper();

    public VehicleStateRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setTimeout(-1);
    }

    private Vehicle parseVehicleFromResultSet(ResultSet rs, int nRow) throws SQLException {
        return Vehicle.builder()
                .id(Objects.toString(rs.getLong("session_id")))
                .status(VehicleStatus.valueOf(rs.getString("status")))
                .capacity(rs.getInt("total_capacity"))
                .residualCapacity(rs.getInt("residual_capacity"))
                .schedule(scheduleJsonMapper.decodeSchedule(rs.getString("schedule_json")))
                .build();
    }

    public List<Vehicle> getVehiclesByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var vehicles = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " driver_id, " +
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json " +
                        " from vehicle_session " +
                        " where session_id in (:ids)",
                params,
                this::parseVehicleFromResultSet);
        return vehicles;
    }

    public void updateVehiclesBatch(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            return;
        }
        var vehiclesList = new ArrayList<>(vehicles);
        jdbcTemplate.getJdbcTemplate().batchUpdate(
                " update vehicle_session " +
                        " set status = cast(? as vehicle_status), " +
                        " total_capacity = ?, " +
                        " residual_capacity = ?, " +
                        " schedule_json = ? " +
                        " where session_id = ? ",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var vehicle = vehiclesList.get(i);
                        ps.setString(1, Objects.toString(vehicle.getStatus()));
                        ps.setInt(2, vehicle.getCapacity());
                        ps.setInt(3, vehicle.getResidualCapacity());
                        ps.setString(4, scheduleJsonMapper.encodeSchedule(vehicle.getSchedule()));
                        ps.setLong(5, Long.parseLong(vehicle.getId()));
                    }

                    @Override public int getBatchSize() {
                        return vehicles.size();
                    }
                });
    }

}
