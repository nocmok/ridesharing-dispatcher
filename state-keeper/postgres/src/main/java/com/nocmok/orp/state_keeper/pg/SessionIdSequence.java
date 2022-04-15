package com.nocmok.orp.state_keeper.pg;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SessionIdSequence {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SessionIdSequence(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long nextId() {
        return jdbcTemplate.getJdbcTemplate().queryForObject("select nextval('session_id_seq')", Long.TYPE);
    }
}
