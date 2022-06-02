package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRequestInfoRequest {

    @JsonProperty("requestId")
    private String requestId;
}
