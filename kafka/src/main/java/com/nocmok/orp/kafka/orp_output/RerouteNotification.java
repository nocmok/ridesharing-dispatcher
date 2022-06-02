package com.nocmok.orp.kafka.orp_output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Нотификация о перестроении маршрута тс
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RerouteNotification {
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("updatedSchedule")
    private List<ScheduleEntry> updatedSchedule;

    @JsonProperty("updatedRoute")
    private List<RouteNode> updatedRoute;
}
