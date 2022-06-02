package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.SessionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetActiveSessionsResponse {

    @JsonProperty("activeSessions")
    private List<SessionInfo> activeSessions;

    @JsonProperty("count")
    public Integer activeSessionsSize() {
        return activeSessions == null ? 0 : activeSessions.size();
    }
}
