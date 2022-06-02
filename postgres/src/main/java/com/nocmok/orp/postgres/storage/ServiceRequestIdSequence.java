package com.nocmok.orp.postgres.storage;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestIdSequence {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public ServiceRequestIdSequence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long nextValue() {
        return jdbcTemplate.getJdbcTemplate().queryForObject("select nextval('request_id_seq')", Long.TYPE);
    }
}
