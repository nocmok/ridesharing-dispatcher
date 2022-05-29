package com.nocmok.orp.postgres.storage.filter;

import java.sql.Timestamp;
import java.time.Instant;

public class InstantField extends AbstractField<Timestamp, Instant> {

    public InstantField(String fieldName) {
        super(fieldName);
    }

    @Override public Timestamp convertValue(Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
