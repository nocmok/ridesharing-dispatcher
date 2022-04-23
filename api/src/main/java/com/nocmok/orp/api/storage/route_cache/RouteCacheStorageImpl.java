package com.nocmok.orp.api.storage.route_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class RouteCacheStorageImpl implements RouteCacheStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public RouteCacheStorageImpl(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private List<RouteNode> parseRouteFromJson(String json) {
        try {
            RouteNode[] routeArray = objectMapper.readValue(json, RouteNode[].class);
            return Arrays.asList(routeArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public List<RouteNode> getRouteCacheBySessionId(String sessionId) {
        var json = jdbcTemplate.queryForObject("select route_json from session_route_cache where session_id = :sessionId",
                Map.of("sessionId", Long.parseLong(sessionId)), String.class);
        return parseRouteFromJson(json);
    }
}
