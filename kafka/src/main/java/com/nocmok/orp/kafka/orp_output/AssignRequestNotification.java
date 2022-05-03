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
 * Сообщение, которое отправляется диспетчером, когда тс приняло запрос на обработку и его план был перестроен.
 * Сообщение может использоваться:
 * 1) Для нотификации водителя о том, что его план перестроен
 * 2) Для нотификации клиента о том, что машина в пути
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRequestNotification {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;

    @JsonProperty("schedule")
    private List<ScheduleEntry> schedule;

    @JsonProperty("routeScheduled")
    private List<RouteNode> routeScheduled;

}
