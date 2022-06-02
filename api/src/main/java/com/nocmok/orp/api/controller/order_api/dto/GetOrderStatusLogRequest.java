package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Objects;

@Data
public class GetOrderStatusLogRequest {

    @JsonProperty(value = "orderId", required = true)
    private String orderId;

    @JsonProperty(value = "page")
    private Integer page = 0;

    @JsonProperty(value = "entriesPerPage")
    private Integer entriesPerPage = 100;

    @JsonProperty(value = "ascending")
    private Boolean ascending = false;

    @JsonSetter("page")
    public void setPage(Integer page) {
        this.page = Objects.requireNonNullElse(page, 0);
    }

    @JsonSetter("entriesPerPage")
    public void setEntriesPerPage(Integer entriesPerPage) {
        this.entriesPerPage = Objects.requireNonNullElse(entriesPerPage, 100);
    }

    @JsonSetter("ascending")
    public void setAscending(Boolean ascending) {
        this.ascending = Objects.requireNonNullElse(ascending, false);
    }
}
