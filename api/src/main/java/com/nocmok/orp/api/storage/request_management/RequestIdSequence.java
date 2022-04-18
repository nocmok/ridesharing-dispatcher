package com.nocmok.orp.api.storage.request_management;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RequestIdSequence {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public RequestIdSequence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long nextValue() {
        return jdbcTemplate.getJdbcTemplate().queryForObject("select nextval('request_id_seq')", Long.TYPE);
    }
}
