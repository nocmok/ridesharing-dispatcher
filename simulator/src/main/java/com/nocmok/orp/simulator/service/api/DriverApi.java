package com.nocmok.orp.simulator.service.api;

import com.nocmok.orp.kafka.orp_input.RequestConfirmationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class DriverApi {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${orp.api.url}")
    private String apiServerUrl;

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

    public void updateOrderStatus(UpdateOrderStatusRequest request) {
        var restTemplate = new RestTemplate();

        RequestEntity<UpdateOrderStatusRequest> requestEntity = RequestEntity
                .post( apiServerUrl + "/driver_api/v0/update_order_status")
                .accept(MediaType.APPLICATION_JSON)
                .body(request);

        restTemplate.exchange(requestEntity, String.class);
    }
}
