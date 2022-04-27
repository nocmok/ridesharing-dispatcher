package com.nocmok.orp.api.storage.request_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.api.storage.request_management.dto.LatLon;
import com.nocmok.orp.api.storage.request_management.dto.OrderStatus;
import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;
import com.nocmok.orp.api.storage.request_management.dto.RoadSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RequestInfoStorageImpl implements RequestInfoStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private RequestIdSequence requestIdSequence;
    private ObjectMapper objectMapper;

    @Autowired
    public RequestInfoStorageImpl(NamedParameterJdbcTemplate jdbcTemplate, RequestIdSequence requestIdSequence,
                                  ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.requestIdSequence = requestIdSequence;
        this.objectMapper = objectMapper;
    }

    @Override public String getIdForRequest() {
        return Long.toString(requestIdSequence.nextValue());
    }

    @Override public List<String> getActiveRequestsIds() {
        return jdbcTemplate.getJdbcTemplate().queryForList("select request_id from service_request where status in ('PENDING', 'SERVING')", Long.class)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private RequestInfo mapResultSetToRequestInfo(ResultSet rs, int rNum) throws SQLException {
        return RequestInfo.builder()
                .requestId(Long.toString(rs.getLong("request_id")))
                .recordedOrigin(new LatLon(
                        rs.getDouble("recorded_origin_latitude"),
                        rs.getDouble("recorded_origin_longitude")
                ))
                .recordedDestination(new LatLon(
                        rs.getDouble("recorded_destination_latitude"),
                        rs.getDouble("recorded_destination_longitude")
                ))
                .pickupRoadSegment(new RoadSegment(
                        rs.getString("pickup_road_segment_start_node_id"),
                        rs.getString("pickup_road_segment_end_node_id")
                ))
                .dropoffRoadSegment(new RoadSegment(
                        rs.getString("dropoff_road_segment_start_node_id"),
                        rs.getString("dropoff_road_segment_end_node_id")
                ))
                .requestedAt(Optional.ofNullable(rs.getTimestamp("requested_at"))
                        .map(Timestamp::toInstant)
                        .orElseThrow(() -> new NullPointerException("requested_at not expected to be null")))
                .detourConstraint(rs.getDouble("detour_constraint"))
                .maxPickupDelaySeconds(rs.getInt("max_pickup_delay_seconds"))
                .load(rs.getInt("load"))
                .status(OrderStatus.valueOf(rs.getString("status")))
                .servingSessionId(Long.toString(rs.getLong("serving_session_id")))
                .build();
    }

    @Override public Optional<RequestInfo> getRequestInfo(String requestId) {
        var params = new HashMap<String, Object>();
        params.put("requestId", Long.parseLong(requestId));
        var requestInfos = jdbcTemplate.query("select * from service_request where request_id = :requestId", params, this::mapResultSetToRequestInfo);
        if (requestInfos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(requestInfos.get(0));
    }
}
