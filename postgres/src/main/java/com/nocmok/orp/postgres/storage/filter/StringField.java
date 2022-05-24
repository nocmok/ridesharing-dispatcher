package com.nocmok.orp.postgres.storage.filter;

public class StringField extends AbstractField<String, String> {

    public StringField(String fieldName) {
        super(fieldName);
    }

    @Override public String convertValue(String value) {
        return value;
    }
}
