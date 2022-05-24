package com.nocmok.orp.postgres.storage.filter;

public class EnumField<T extends Enum<?>> extends AbstractField<String, T> {

    private final String enumName;

    public EnumField(String fieldName, String enumName) {
        super(fieldName);
        this.enumName = enumName;
    }

    @Override public String convertValue(T value) {
        return value.name();
    }

    @Override public String createPlaceHolder(String paramName) {
        return ":" + paramName + "::" + enumName;
    }
}
