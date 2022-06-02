package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.SessionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSessionInfoResponse {

    @JsonProperty("sessionInfo")
    private SessionInfo sessionInfo;
}
