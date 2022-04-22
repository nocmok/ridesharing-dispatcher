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
public class GetActiveSessionsIdsResponse {

    @JsonProperty("activeSessionsIds")
    private List<String> activeSessionsIds;

    @JsonProperty("count")
    public Integer getActiveSessionsIdsCount() {
        return activeSessionsIds == null ? 0 : activeSessionsIds.size();
    }
}
