package com.nocmok.orp.api.storage.request_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

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

    private String asJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public RequestInfo insertRequest(RequestInfo requestInfo) {
        var requestId = requestIdSequence.nextValue();
        requestInfo.setRequestId(Objects.toString(requestId));

        var params = new HashMap<String, Object>();
        params.put("requestId", requestId);
        params.put("recordedOrigin", asJson(requestInfo.getRecordedOrigin()));
        params.put("recordedDestination", asJson(requestInfo.getRecordedDestination()));
        params.put("pickupRoadSegment", asJson(requestInfo.getPickupRoadSegment()));
        params.put("dropoffRoadSegment", asJson(requestInfo.getDropoffRoadSegment()));
        params.put("detourConstraint", requestInfo.getDetourConstraint());
        params.put("maxPickupDelaySeconds", requestInfo.getMaxPickupDelaySeconds());
        params.put("requestedAt", Optional.ofNullable(requestInfo.getRequestedAt()).map(Timestamp::from).orElse(null));
        params.put("load", requestInfo.getLoad());

        jdbcTemplate.update("insert into request_info " +
                " (" +
                " request_id, " +
                " recorded_origin," +
                " recorded_destination, " +
                " pickup_road_segment," +
                " dropoff_road_segment, " +
                " detour_constraint," +
                " max_pickup_delay_seconds, " +
                " requested_at, " +
                " load" +
                ") " +
                "values " +
                "(" +
                " :requestId, " +
                " :recordedOrigin, " +
                " :recordedDestination, " +
                " :pickupRoadSegment, " +
                " :dropoffRoadSegment, " +
                " :detourConstraint, " +
                " :maxPickupDelaySeconds, " +
                " :requestedAt, " +
                " :load " +
                ")", params);

        return requestInfo;
    }
}
