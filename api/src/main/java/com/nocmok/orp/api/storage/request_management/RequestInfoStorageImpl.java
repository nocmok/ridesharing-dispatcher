package com.nocmok.orp.api.storage.request_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
