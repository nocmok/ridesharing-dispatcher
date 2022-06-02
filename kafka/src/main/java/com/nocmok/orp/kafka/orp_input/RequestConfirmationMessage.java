package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Подтверждение запроса от клиента, отправленное водителем
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestConfirmationMessage {

    @JsonProperty("reservationId")
    private String reservationId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;
}
