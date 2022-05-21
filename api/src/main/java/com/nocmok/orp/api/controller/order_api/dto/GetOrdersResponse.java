package com.nocmok.orp.api.controller.order_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.RequestInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetOrdersResponse {

    @JsonProperty("orders")
    public List<RequestInfo> orders;

    @JsonGetter("size")
    public Integer size() {
        return orders == null ? 0 : orders.size();
    }
}
