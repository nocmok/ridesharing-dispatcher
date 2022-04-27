package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.RequestInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRequestInfoResponse {

    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;
}
