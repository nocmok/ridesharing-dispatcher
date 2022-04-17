package com.nocmok.orp.solver.api;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleNode {

    @JsonProperty("deadline")
    private Instant deadline;
    @JsonProperty("load")
    private Integer load;
    @JsonProperty("nodeId")
    private String nodeId;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("kind")
    private ScheduleNodeKind kind;
    @JsonProperty("orderId")
    private String orderId;

}
