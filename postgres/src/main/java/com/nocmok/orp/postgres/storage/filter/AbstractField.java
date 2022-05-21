package com.nocmok.orp.postgres.storage.filter;

public abstract class AbstractField<T, V> implements Field<T, V> {

    private final String fieldName;

    public AbstractField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override public String getFieldName() {
        return fieldName;
    }

    @Override abstract public T convertValue(V value);
}
