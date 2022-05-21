package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequestFilter {

    @JsonProperty("filtering")
    private List<OneOf> filtering;

    @JsonProperty("ordering")
    private List<OrderBy> ordering;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("pageSize")
    private Long pageSize;
}
