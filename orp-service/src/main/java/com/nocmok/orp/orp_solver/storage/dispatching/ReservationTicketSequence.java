package com.nocmok.orp.orp_solver.storage.dispatching;

import org.springframework.jdbc.core.JdbcTemplate;

public class ReservationTicketSequence {

    private JdbcTemplate jdbcTemplate;

    public ReservationTicketSequence(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String nextValue() {
        return jdbcTemplate.query("select nextval('reservation_id_seq') as id", (rs, nr) -> rs.getLong("id"))
                .stream()
                .map(Object::toString)
                .findFirst().get();
    }
}
