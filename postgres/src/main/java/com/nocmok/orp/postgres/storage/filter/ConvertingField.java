package com.nocmok.orp.postgres.storage.filter;

import java.util.function.Function;

public class ConvertingField<T, V> extends AbstractField<T, V> {

    private final Function<V, T> converter;

    public ConvertingField(String fieldName, Function<V, T> converter) {
        super(fieldName);
        this.converter = converter;
    }

    @Override public T convertValue(V value) {
        return converter.apply(value);
    }
}
