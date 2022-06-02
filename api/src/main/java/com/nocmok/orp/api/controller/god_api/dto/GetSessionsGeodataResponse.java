package com.nocmok.orp.api.controller.god_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSessionsGeodataResponse {

    @JsonProperty("sessions")
    private List<SessionGeodata> sessions;

    @JsonProperty("count")
    public Integer getSessionsSize() {
        return sessions == null ? 0 : sessions.size();
    }
}
