package com.nocmok.orp.postgres.storage;

import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class ServiceRequestStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceRequestStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ServiceRequest mapResultSetToServiceRequest(ResultSet rs, int nRow) throws SQLException {
        return new ServiceRequest(
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
                rs.getInt("load"),
                OrderStatus.valueOf(rs.getString("status")),
                Long.toString(rs.getLong("serving_session_id"))
        );
    }

    public Optional<ServiceRequest> getRequestById(String id) {
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
                        " load," +
                        " status," +
                        " serving_session_id " +
                        " from service_request " +
                        " where request_id = :requestId",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    public Optional<ServiceRequest> getRequestByIdForUpdate(String id) {
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
                        " load," +
                        " status," +
                        " serving_session_id " +
                        " from service_request " +
                        " where request_id = :requestId " +
                        " for update ",
                params, this::mapResultSetToServiceRequest);
        return requests.stream().findFirst();
    }

    public void updateRequestStatus(String requestId, OrderStatus updatedStatus) {
        var params = new HashMap<String, Object>();
        params.put("status", updatedStatus.name());
        params.put("requestId", Long.parseLong(requestId));
        jdbcTemplate.update(
                " update service_request " +
                        " set status = cast(:status as service_request_status) " +
                        " where request_id = :requestId ", params);
    }

    public void updateServingSessionId(String requestId, String sessionId) {
        var params = new HashMap<String, Object>();
        params.put("servingSessionId", Long.parseLong(sessionId));
        params.put("requestId", Long.parseLong(requestId));
        jdbcTemplate.update(
                " update service_request " +
                        " set serving_session_id = :servingSessionId " +
                        " where request_id = :requestId ", params);
    }
}
