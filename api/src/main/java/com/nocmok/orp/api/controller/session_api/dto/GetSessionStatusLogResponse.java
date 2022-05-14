package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.controller.common_dto.SessionStatusLogEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetSessionStatusLogResponse {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("statusLog")
    private List<SessionStatusLogEntry> statusLog;

    @JsonGetter("size")
    public int size() {
        return statusLog == null ? 0 : statusLog.size();
    }
}
