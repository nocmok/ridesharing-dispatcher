package com.nocmok.orp.simulator.service.api;

import com.nocmok.orp.kafka.orp_input.RequestConfirmationMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DriverApi {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public DriverApi(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void confirmRequest(ServiceRequestConfirmation confirmation) {
        var confirmationMessage = RequestConfirmationMessage.builder()
                .sessionId(confirmation.getSessionId())
                .serviceRequestId(confirmation.getRequestId())
                .reservationId(confirmation.getReservationId())
                .build();

        kafkaTemplate.send(new ProducerRecord<>(
                "orp.input",
                null,
                confirmation.getSessionId(),
                confirmationMessage,
                List.of(new RecordHeader("__TypeId__", ServiceRequestConfirmation.class.getName().getBytes(StandardCharsets.UTF_8)))
        ));
    }
}
