package com.nocmok.orp.orp_solver.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

public class RequestMatchingOutboxStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public RequestMatchingOutboxStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String vehicleId, String requestId) {
        var params = new HashMap<String, Object>();
        params.put("vehicleId", Long.parseLong(vehicleId));
        params.put("requestId", Long.parseLong(requestId));
        jdbcTemplate.update(" insert into request_matching_outbox(session_id, request_id, sent_at) " +
                " values(:vehicleId, :requestId, null) ", params);
    }
}
