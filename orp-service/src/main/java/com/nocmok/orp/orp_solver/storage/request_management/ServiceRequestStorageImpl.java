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
                rs.getDouble("recorded_origin_latitude"),
                rs.getDouble("recorded_origin_longitude"),
                rs.getDouble("recorded_destination_latitude"),
                rs.getDouble("recorded_destination_longitude"),
                rs.getString("pickup_road_segment_start_node_id"),
                rs.getString("pickup_road_segment_end_node_id"),
                rs.getString("dropoff_road_segment_start_node_id"),
                rs.getString("dropoff_road_segment_end_node_id"),
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
                        " recorded_origin_latitude," +
                        " recorded_origin_longitude," +
                        " recorded_destination_latitude," +
                        " recorded_destination_longitude," +
                        " pickup_road_segment_start_node_id," +
                        " pickup_road_segment_end_node_id," +
                        " dropoff_road_segment_start_node_id," +
                        " dropoff_road_segment_end_node_id," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load " +
                        " from service_request " +
                        " where request_id = :requestId",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    @Override
    public void insertRequest(ServiceRequestDto request) {
        var params = new HashMap<String, Object>();
        params.put("request_id", Long.parseLong(request.getRequestId()));
        params.put("recorded_origin_latitude", request.getRecordedOriginLatitude());
        params.put("recorded_origin_longitude", request.getRecordedOriginLongitude());
        params.put("recorded_destination_latitude", request.getRecordedDestinationLatitude());
        params.put("recorded_destination_longitude", request.getRecordedDestinationLongitude());
        params.put("pickup_road_segment_start_node_id", request.getPickupRoadSegmentStartNodeId());
        params.put("pickup_road_segment_end_node_id", request.getPickupRoadSegmentEndNodeId());
        params.put("dropoff_road_segment_start_node_id", request.getDropOffRoadSegmentStartNodeId());
        params.put("dropoff_road_segment_end_node_id", request.getDropOffRoadSegmentEndNodeId());
        params.put("detour_constraint", request.getDetourConstraint());
        params.put("max_pickup_delay_seconds", request.getMaxPickupDelaySeconds());
        params.put("requested_at", Optional.ofNullable(request.getRequestedAt()).map(Timestamp::from)
                .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")));
        params.put("load", request.getLoad());
        jdbcTemplate.update(" insert into service_request " +
                        " ( " +
                        " request_id," +
                        " recorded_origin_latitude," +
                        " recorded_origin_longitude," +
                        " recorded_destination_latitude," +
                        " recorded_destination_longitude," +
                        " pickup_road_segment_start_node_id," +
                        " pickup_road_segment_end_node_id," +
                        " dropoff_road_segment_start_node_id," +
                        " dropoff_road_segment_end_node_id," +
                        " detour_constraint," +
                        " max_pickup_delay_seconds," +
                        " requested_at," +
                        " load " +
                        " ) " +
                        " values " +
                        " ( " +
                        "   :request_id, " +
                        "   :recorded_origin_latitude, " +
                        "   :recorded_origin_longitude, " +
                        "   :recorded_destination_latitude, " +
                        "   :recorded_destination_longitude, " +
                        "   :pickup_road_segment_start_node_id, " +
                        "   :pickup_road_segment_end_node_id, " +
                        "   :dropoff_road_segment_start_node_id, " +
                        "   :dropoff_road_segment_end_node_id, " +
                        "   :detour_constraint, " +
                        "   :max_pickup_delay_seconds, " +
                        "   :requested_at, " +
                        "   :load " +
                        " ) ",
                params);
    }
}
