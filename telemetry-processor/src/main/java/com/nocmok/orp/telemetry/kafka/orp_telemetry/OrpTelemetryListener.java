package com.nocmok.orp.telemetry.kafka.orp_telemetry;

import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.service.StateActuator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@KafkaListener(
        topics = {"orp.telemetry"},
        containerFactory = "orpTelemetryKafkaListenerContainerFactory",
        batch = "true"
)
public class OrpTelemetryListener {

    private final StateActuator stateActuator;

    @Autowired
    public OrpTelemetryListener(StateActuator stateActuator) {
        this.stateActuator = stateActuator;
    }

    @KafkaHandler
    public void processTelemetry(@Payload List<?> batch) {
        List<VehicleTelemetry> telemetry = batch.stream()
                .filter(t -> t instanceof VehicleTelemetry)
                .map(t -> (VehicleTelemetry) t)
                .collect(Collectors.toUnmodifiableList());

        if (telemetry.isEmpty()) {
            log.warn("malformed messages received in orp.telemetry topic, skip them ...");
            return;
        }

        stateActuator.updateStateBatch(telemetry);
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownType(@Payload Object unknown) {
        log.warn("get unknown payload from orp.telemetry topic\n" + unknown);
    }
}
