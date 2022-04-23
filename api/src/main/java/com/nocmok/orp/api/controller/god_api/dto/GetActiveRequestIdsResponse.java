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
public class GetActiveRequestIdsResponse {

    @JsonProperty("requestIds")
    private List<String> requestIds;

    @JsonProperty("count")
    public Integer requestIdsSize() {
        return requestIds.size();
    }
}
