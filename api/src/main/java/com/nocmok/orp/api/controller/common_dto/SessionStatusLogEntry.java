package com.nocmok.orp.api.controller.common_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class SessionStatusLogEntry {

    @JsonProperty("status")
    private String status;

    @JsonProperty("updatedAt")
    private Instant updatedAt;
}
