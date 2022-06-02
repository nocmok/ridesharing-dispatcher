package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.RequestFilter;
import lombok.Data;

@Data
public class GetSessionsRequest {

    @JsonProperty("filter")
    private RequestFilter filter;
}
