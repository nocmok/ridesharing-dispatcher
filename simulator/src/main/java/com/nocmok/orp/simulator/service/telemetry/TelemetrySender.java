package com.nocmok.orp.simulator.service.telemetry;

import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TelemetrySender {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public TelemetrySender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTelemetry(Telemetry telemetry) {
        var telemetryMessage = new VehicleTelemetryMessage(
                telemetry.getSessionId(),
                telemetry.getLatitude(),
                telemetry.getLongitude(),
                telemetry.getAccuracy(),
                telemetry.getRecordedAt()
        );

        kafkaTemplate.send(new ProducerRecord<>(
                "orp.telemetry",
                null,
                telemetry.getSessionId(),
                telemetryMessage,
                List.of(new RecordHeader("__TypeId__", VehicleTelemetryMessage.class.getName().getBytes(StandardCharsets.UTF_8)))
        ));
    }
}
