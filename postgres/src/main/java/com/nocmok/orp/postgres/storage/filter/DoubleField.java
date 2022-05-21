package com.nocmok.orp.postgres.storage.filter;

public class DoubleField extends AbstractField<Double, Double>{

    public DoubleField(String fieldName) {
        super(fieldName);
    }

    @Override public Double convertValue(Double value) {
        return value;
    }
}
