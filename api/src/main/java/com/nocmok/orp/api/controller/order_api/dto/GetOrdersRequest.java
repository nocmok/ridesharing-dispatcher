package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.RequestFilter;
import lombok.Data;

@Data
public class GetOrdersRequest {

    @JsonProperty("filter")
    private RequestFilter filter;
}
