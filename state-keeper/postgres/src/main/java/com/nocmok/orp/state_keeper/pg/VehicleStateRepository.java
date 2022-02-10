package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;
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
import java.util.function.Supplier;

class VehicleStateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final ScheduleJsonMapper scheduleJsonMapper;
    private final RouteJsonMapper routeJsonMapper;

    public VehicleStateRepository(DataSource dataSource, ObjectMapper objectMapper) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setTimeout(-1);
        this.scheduleJsonMapper = new ScheduleJsonMapper(objectMapper);
        this.routeJsonMapper = new RouteJsonMapper(objectMapper);
    }

    private static <T, R> R ifNotNull(T value, Supplier<R> alternative) {
        return value == null ? null : alternative.get();
    }

    private Vehicle parseVehicleFromResultSet(ResultSet rs, int nRow) throws SQLException {
        return Vehicle.builder()
                .id(Objects.toString(rs.getLong("session_id")))
                .status(VehicleStatus.valueOf(rs.getString("status")))
                .capacity(rs.getInt("total_capacity"))
                .residualCapacity(rs.getInt("residual_capacity"))
                .schedule(scheduleJsonMapper.decodeSchedule(rs.getString("schedule_json")))
                .routeScheduled(routeJsonMapper.decodeRoute(rs.getString("route_json")))
                .roadBinding(new GraphBinding(
                        new GraphRoad(
                                new GraphNode(
                                        rs.getInt("road_start_node_id"),
                                        new GCS(rs.getDouble("road_start_node_lat"), rs.getDouble("road_start_node_lon"))),
                                new GraphNode(
                                        rs.getInt("road_end_node_id"),
                                        new GCS(rs.getDouble("road_end_node_lat"), rs.getDouble("road_end_node_lon"))),
                                rs.getDouble("road_cost")
                        ),
                        rs.getDouble("road_progress")
                ))
                .gcs(new GCS(rs.getDouble("lat"), rs.getDouble("lon")))
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
                        " road_start_node_id, " +
                        " road_start_node_lat, " +
                        " road_start_node_lon, " +
                        " road_end_node_id, " +
                        " road_end_node_lat, " +
                        " road_end_node_lon, " +
                        " road_cost, " +
                        " road_progress, " +
                        " lat, " +
                        " lon " +
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
                        " road_start_node_id = coalesce(?, road_start_node_id), " +
                        " road_start_node_lat = coalesce(?, road_start_node_lat), " +
                        " road_start_node_lon = coalesce(?, road_start_node_lon), " +
                        " road_end_node_id = coalesce(?, road_end_node_id), " +
                        " road_end_node_lat = coalesce(?, road_end_node_lat), " +
                        " road_end_node_lon = coalesce(?, road_end_node_lon), " +
                        " road_cost = coalesce(?, road_cost), " +
                        " road_progress = coalesce(?, road_progress), " +
                        " lat = coalesce(?, lat), " +
                        " lon = coalesce(?, lon) " +
                        " where session_id = ? ",
                new BatchPreparedStatementSetter() {
                    @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var vehicle = vehiclesList.get(i);
                        ps.setString(1, ifNotNull(vehicle.getStatus(), () -> Objects.toString(vehicle.getStatus())));
                        ps.setObject(2, vehicle.getCapacity(), Types.BIGINT);
                        ps.setObject(3, vehicle.getResidualCapacity(), Types.BIGINT);
                        ps.setString(4, ifNotNull(vehicle.getSchedule(), () -> scheduleJsonMapper.encodeSchedule(vehicle.getSchedule())));
                        ps.setString(5, ifNotNull(vehicle.getRouteScheduled(), () -> routeJsonMapper.encodeRoute(vehicle.getRouteScheduled())));
                        ps.setObject(6,
                                ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getStartNode().getNodeId()), Types.BIGINT);
                        ps.setObject(7, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getStartNode().getCoordinates().lat()),
                                Types.DOUBLE);
                        ps.setObject(8, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getStartNode().getCoordinates().lon()),
                                Types.DOUBLE);
                        ps.setObject(9,
                                ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getEndNode().getNodeId()), Types.BIGINT);
                        ps.setObject(10, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getEndNode().getCoordinates().lat()),
                                Types.DOUBLE);
                        ps.setObject(11, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getEndNode().getCoordinates().lon()),
                                Types.DOUBLE);
                        ps.setObject(12, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getRoad().getCost()), Types.DOUBLE);
                        ps.setObject(13, ifNotNull(vehicle.getRoadBinding(), () -> vehicle.getRoadBinding().getProgress()), Types.DOUBLE);
                        ps.setObject(14, ifNotNull(vehicle.getGCS(), () -> vehicle.getGCS().lat()), Types.DOUBLE);
                        ps.setObject(15, ifNotNull(vehicle.getGCS(), () -> vehicle.getGCS().lon()), Types.DOUBLE);
                        ps.setLong(16, Long.parseLong(vehicle.getId()));
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
                        " road_start_node_id, " +
                        " road_start_node_lat, " +
                        " road_start_node_lon, " +
                        " road_end_node_id, " +
                        " road_end_node_lat, " +
                        " road_end_node_lon, " +
                        " road_cost, " +
                        " road_progress, " +
                        " distance_scheduled, " +
                        " lat, " +
                        " lon " +
                        " from vehicle_session " +
                        " where completed_at is null",
                this::parseVehicleFromResultSet);
        return vehicles;
    }
}
