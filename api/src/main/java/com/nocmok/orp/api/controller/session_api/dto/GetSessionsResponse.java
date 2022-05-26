package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.SessionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetSessionsResponse {

    @JsonProperty("sessions")
    private List<SessionDto> sessions;

    @JsonGetter("size")
    private Integer size() {
        return sessions == null ? 0 : sessions.size();
    }
}
