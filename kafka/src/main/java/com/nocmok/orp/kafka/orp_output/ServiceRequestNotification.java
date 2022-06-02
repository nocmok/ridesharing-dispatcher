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
 * Сообщение, которое отправляется
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestNotification {

    /**
     * Идентификатор тс которому будет отправлено уведомление
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * Идентификатор запроса с которым придет уведомление тс
     */
    @JsonProperty("requestId")
    private String requestId;

    /**
     * Идентификатор резервации тс в рамках которой отправляется запрос на обслуживание.
     * <p>
     * При поступлении от тс подтверждение запроса проверяется резервация по этому идентификатору.
     * На тс реально назначается запрос, только если запись с резервацией не истекла
     */
    @JsonProperty("reservationId")
    private String reservationId;

    @JsonProperty("augmentedSchedule")
    private List<ScheduleEntry> augmentedSchedule;

    @JsonProperty("augmentedRoute")
    private List<RouteNode> augmentedRoute;
}
