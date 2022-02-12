package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class VehicleStateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final ScheduleJsonMapper scheduleJsonMapper;
    private final RouteJsonMapper routeJsonMapper;
    private final GeotagJsonMapper geotagJsonMapper;

    public VehicleStateRepository(DataSource dataSource, ObjectMapper objectMapper) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setTimeout(-1);
        this.scheduleJsonMapper = new ScheduleJsonMapper(objectMapper);
        this.routeJsonMapper = new RouteJsonMapper(objectMapper);
        this.geotagJsonMapper = new GeotagJsonMapper(objectMapper);
    }

    private Vehicle parseVehicleFromResultSet(ResultSet rs, int nRow) throws SQLException {
        var schedule = scheduleJsonMapper.decodeSchedule(rs.getString("schedule_json"));
        var routeScheduled = routeJsonMapper.decodeRoute(rs.getString("route_json"));
        var geotag = geotagJsonMapper.decodeGeotag(rs.getString("geotag_json"));
        return Vehicle.builder()
                .id(Objects.toString(rs.getLong("session_id")))
                .status(VehicleStatus.valueOf(rs.getString("status")))
                .capacity(rs.getInt("total_capacity"))
                .residualCapacity(rs.getInt("residual_capacity"))
                .schedule(schedule)
                .routeScheduled(routeScheduled)
                .roadBinding(Optional.ofNullable(geotag).map(Geotag::getGraphBinding).orElse(null))
                .gcs(Optional.ofNullable(geotag).map(Geotag::getGcs).orElse(null))
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
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json, " +
                        " route_json, " +
                        " geotag_json " +
                        " from vehicle_session " +
                        " where session_id in (:ids)",
                params,
                this::parseVehicleFromResultSet);
        return vehicles;
    }

    public void updateVehiclesBatch(List<? extends com.nocmok.orp.core_api.Vehicle> vehicles) {
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
                        " schedule_json = coalesce(?, schedule_json), " +
                        " route_json = coalesce(?, route_json), " +
                        " geotag_json = coalesce(?, geotag_json) " +
                        " where session_id = ? ",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var vehicle = vehiclesList.get(i);
                        ps.setString(1, Optional.ofNullable(vehicle.getStatus()).map(Objects::toString).orElse(null));
                        ps.setObject(2, vehicle.getCapacity(), Types.BIGINT);
                        ps.setObject(3, vehicle.getResidualCapacity(), Types.BIGINT);
                        ps.setString(4, Optional.ofNullable(vehicle.getSchedule()).map(scheduleJsonMapper::encodeSchedule).orElse(null));
                        ps.setString(5, Optional.ofNullable(vehicle.getRouteScheduled()).map(routeJsonMapper::encodeRoute).orElse(null));
                        ps.setString(6, geotagJsonMapper.encodeGeotag(new Geotag(vehicle.getRoadBinding(), vehicle.getGCS())));
                        ps.setLong(7, Long.parseLong(vehicle.getId()));
                    }

                    @Override public int getBatchSize() {
                        return vehicles.size();
                    }
                });
    }

    public List<String> getActiveVehiclesIds() {
        return jdbcTemplate.query("select session_id from vehicle_session where completed_at is null",
                (rs, rowNum) -> Objects.toString(rs.getLong("session_ids")));
    }

    public List<Vehicle> getActiveVehicles() {
        var vehicles = jdbcTemplate.query(
                " select " +
                        " session_id, " +
                        " status, " +
                        " total_capacity," +
                        " residual_capacity, " +
                        " schedule_json, " +
                        " route_json, " +
                        " geotag_json " +
                        " from vehicle_session " +
                        " where completed_at is null",
                this::parseVehicleFromResultSet);
        return vehicles;
    }
}
