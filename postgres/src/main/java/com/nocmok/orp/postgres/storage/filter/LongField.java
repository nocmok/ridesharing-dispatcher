package com.nocmok.orp.postgres.storage.filter;

public class LongField extends AbstractField<Long, Long> {

    public LongField(String fieldName) {
        super(fieldName);
    }

    @Override public Long convertValue(Long value) {
        return value;
    }
}
