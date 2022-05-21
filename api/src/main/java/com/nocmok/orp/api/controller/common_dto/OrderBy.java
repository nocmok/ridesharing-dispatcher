package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderBy {

    @JsonProperty("fieldName")
    private String fieldName;

    @JsonProperty("ascending")
    private boolean ascending;
}
