package com.nocmok.orp.postgres.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.solver.api.RouteNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RouteCacheStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public RouteCacheStorage(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private String asJson(List<?> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<RouteNode> fromJson(String json) {
        try {
            RouteNode[] routeArray = objectMapper.readValue(json, RouteNode[].class);
            return Arrays.asList(routeArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRouteCacheBySessionId(String sessionId, List<RouteNode> route) {
        var params = new HashMap<String, Object>();
        params.put("sessionId", Long.parseLong(sessionId));
        params.put("routeJson", asJson(route));
        jdbcTemplate.update(" insert into session_route_cache(session_id, route_json) " +
                " values (:sessionId, :routeJson) " +
                " on conflict (session_id) do update " +
                " set route_json = :routeJson ", params);
    }

    public List<RouteNode> getRouteCacheBySessionId(String sessionId) {
        List<String> jsons = jdbcTemplate.query("select route_json from session_route_cache where session_id = :sessionId",
                Map.of("sessionId", Long.parseLong(sessionId)), (rs, nRow) -> rs.getString("route_json"));
        if (jsons.isEmpty()) {
            return Collections.emptyList();
        }
        return fromJson(jsons.get(0));
    }
}
