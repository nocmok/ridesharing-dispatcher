package com.nocmok.orp.postgres.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SessionReservationSequence {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionReservationSequence(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String nextValue() {
        return jdbcTemplate.query("select nextval('reservation_id_seq') as id", (rs, nr) -> rs.getLong("id"))
                .stream()
                .map(Object::toString)
                .findFirst().get();
    }
}
