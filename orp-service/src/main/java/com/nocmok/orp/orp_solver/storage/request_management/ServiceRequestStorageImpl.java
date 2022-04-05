package com.nocmok.orp.orp_solver.storage.request_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class ServiceRequestStorageImpl implements ServiceRequestStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceRequestStorageImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ServiceRequestDto mapResultSetToServiceRequest(ResultSet rs, int nRow) throws SQLException {
        return new ServiceRequestDto(
                Long.toString(rs.getLong("request_id")),
                rs.getInt("pickup_node_id"),
                rs.getDouble("pickup_node_latitude"),
                rs.getDouble("pickup_node_longitude"),
                rs.getInt("dropoff_node_id"),
                rs.getDouble("dropoff_node_latitude"),
                rs.getDouble("dropoff_node_longitude"),
                Optional.ofNullable(rs.getTimestamp("requested_at")).map(Timestamp::toInstant)
                        .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")),
                rs.getDouble("detour_constraint"),
                rs.getInt("max_pickup_delay_seconds"),
                rs.getInt("load")
        );
    }

    @Override
    public Optional<ServiceRequestDto> getRequestById(String id) {
        var params = new HashMap<String, Object>();
        params.put("requestId", Long.parseLong(id));
        var requests = jdbcTemplate.query(
                " select " +
                        " request_id," +
                        " pickup_node_id," +
                        " pickup_node_latitude," +
                        " pickup_node_longitude," +
                        " dropoff_node_id," +
                        " dropoff_node_latitude," +
                        " dropoff_node_longitude," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load " +
                        " from request " +
                        " where request_id = :requestId",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    @Override
    public void insertRequest(ServiceRequestDto request) {
        var params = new HashMap<String, Object>();
        params.put("request_id", Long.parseLong(request.getRequestId()));
        params.put("pickup_node_id", request.getPickupNodeId());
        params.put("pickup_node_latitude", request.getPickupLat());
        params.put("pickup_node_longitude", request.getPickupLon());
        params.put("dropoff_node_id", request.getDropoffNodeId());
        params.put("dropoff_node_latitude", request.getDropoffLat());
        params.put("dropoff_node_longitude", request.getDropoffLon());
        params.put("detour_constraint", request.getDetourConstraint());
        params.put("max_pickup_delay_seconds", request.getMaxPickupDelaySeconds());
        params.put("requested_at", Optional.ofNullable(request.getRequestedAt()).map(Timestamp::from)
                .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")));
        params.put("load", request.getLoad());
        jdbcTemplate.update(" insert into request " +
                        " ( " +
                        "   request_id, " +
                        "   pickup_node_id, " +
                        "   pickup_node_latitude, " +
                        "   pickup_node_longitude, " +
                        "   dropoff_node_id, " +
                        "   dropoff_node_latitude, " +
                        "   dropoff_node_longitude, " +
                        "   detour_constraint, " +
                        "   max_pickup_delay_seconds, " +
                        "   requested_at, " +
                        "   load " +
                        " ) " +
                        " values " +
                        " ( " +
                        "   :request_id, " +
                        "   :pickup_node_id, " +
                        "   :pickup_node_latitude, " +
                        "   :pickup_node_longitude, " +
                        "   :dropoff_node_id, " +
                        "   :dropoff_node_latitude, " +
                        "   :dropoff_node_longitude, " +
                        "   :detour_constraint, " +
                        "   :max_pickup_delay_seconds, " +
                        "   :requested_at, " +
                        "   :load " +
                        " ) ",
                params);
    }
}
