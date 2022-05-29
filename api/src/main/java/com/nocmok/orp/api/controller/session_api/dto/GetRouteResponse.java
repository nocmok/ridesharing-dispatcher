package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.Node;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetRouteResponse {

    @JsonProperty("route")
    private List<Node> route;
}
