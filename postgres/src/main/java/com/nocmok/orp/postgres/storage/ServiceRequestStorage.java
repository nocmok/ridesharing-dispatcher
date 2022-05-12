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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ServiceRequestStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ServiceRequestIdSequence serviceRequestIdSequence;

    @Autowired
    public ServiceRequestStorage(NamedParameterJdbcTemplate jdbcTemplate, ServiceRequestIdSequence serviceRequestIdSequence) {
        this.jdbcTemplate = jdbcTemplate;
        this.serviceRequestIdSequence = serviceRequestIdSequence;
    }

    public String getIdForRequest() {
        return Long.toString(serviceRequestIdSequence.nextValue());
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

    public ServiceRequest storeRequest(ServiceRequest serviceRequest) {
        serviceRequest.setRequestId(getIdForRequest());
        var params = new HashMap<String, Object>();
        params.put("request_id", Long.parseLong(serviceRequest.getRequestId()));
        params.put("recorded_origin_latitude", serviceRequest.getRecordedOriginLatitude());
        params.put("recorded_origin_longitude", serviceRequest.getRecordedOriginLongitude());
        params.put("recorded_destination_latitude", serviceRequest.getRecordedDestinationLatitude());
        params.put("recorded_destination_longitude", serviceRequest.getRecordedDestinationLongitude());
        params.put("pickup_road_segment_start_node_id", serviceRequest.getPickupRoadSegmentStartNodeId());
        params.put("pickup_road_segment_end_node_id", serviceRequest.getPickupRoadSegmentEndNodeId());
        params.put("dropoff_road_segment_start_node_id", serviceRequest.getDropOffRoadSegmentStartNodeId());
        params.put("dropoff_road_segment_end_node_id", serviceRequest.getDropOffRoadSegmentEndNodeId());
        params.put("detour_constraint", serviceRequest.getDetourConstraint());
        params.put("max_pickup_delay_seconds", serviceRequest.getMaxPickupDelaySeconds());
        params.put("requested_at", Optional.ofNullable(serviceRequest.getRequestedAt()).map(Timestamp::from)
                .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")));
        params.put("load", serviceRequest.getLoad());
        params.put("status", Objects.requireNonNullElse(serviceRequest.getStatus(), OrderStatus.PENDING).name());
        params.put("serving_session_id", serviceRequest.getServingSessionId() == null ? null : Long.parseLong(serviceRequest.getServingSessionId()));
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
                        " load," +
                        " status," +
                        " serving_session_id " +
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
                        "   :load," +
                        "   cast(:status as service_request_status)," +
                        "   :serving_session_id " +
                        " ) ",
                params);
        return serviceRequest;
    }

    public List<String> getActiveRequestsIds() {
        return jdbcTemplate.getJdbcTemplate().queryForList("select request_id from service_request where status in ('PENDING', 'SERVING')", Long.class)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
