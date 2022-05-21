package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OneOf {

    @JsonProperty("fieldName")
    private String fieldName;

    @JsonProperty("values")
    private List<String> values;
}
