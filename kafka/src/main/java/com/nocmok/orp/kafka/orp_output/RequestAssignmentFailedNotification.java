package com.nocmok.orp.kafka.orp_output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RequestAssignmentFailedNotification {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;
}
