package com.nocmok.orp.postgres.storage.filter;

public interface Field<T, V> {
    String getFieldName();
    T convertValue(V value);
    String createPlaceHolder(String paramName);
}
