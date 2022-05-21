package com.nocmok.orp.postgres.storage.filter;

public class IntegerField extends AbstractField<Integer, Integer> {

    public IntegerField(String fieldName) {
        super(fieldName);
    }

    @Override public Integer convertValue(Integer value) {
        return value;
    }
}
