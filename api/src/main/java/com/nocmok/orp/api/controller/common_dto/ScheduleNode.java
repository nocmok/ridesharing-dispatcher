package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.solver.api.ScheduleEntryKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleNode {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("kind")
    private ScheduleEntryKind kind;
}
