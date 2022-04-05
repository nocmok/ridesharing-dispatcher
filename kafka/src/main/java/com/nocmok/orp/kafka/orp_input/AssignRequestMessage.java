package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Подтверждение запроса от клиента, отправленное водителем
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssignRequestMessage {

    @JsonProperty("reservationId")
    private String reservationId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;
}
