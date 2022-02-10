package com.nocmok.orp.telemetry.kafka.orp_telemetry;

import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetryMessage;
import com.nocmok.orp.telemetry.kafka.orp_telemetry.mapper.VehicleTelemetryMapper;
import com.nocmok.orp.telemetry.service.VehiclePositionBatchUpdater;
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

    private VehiclePositionBatchUpdater vehiclePositionBatchUpdater;
    private VehicleTelemetryMapper mapper = new VehicleTelemetryMapper();

    @Autowired
    public OrpTelemetryListener(VehiclePositionBatchUpdater vehiclePositionBatchUpdater) {
        this.vehiclePositionBatchUpdater = vehiclePositionBatchUpdater;
    }

    @KafkaHandler
    public void processTelemetry(@Payload List<?> batch) {
        List<VehicleTelemetryMessage> telemetry = batch.stream()
                .filter(t -> t instanceof VehicleTelemetryMessage)
                .map(t -> (VehicleTelemetryMessage) t)
                .collect(Collectors.toUnmodifiableList());

        if (telemetry.isEmpty()) {
            log.warn("malformed messages received in orp.telemetry topic, skip them ...");
            return;
        }

        vehiclePositionBatchUpdater.batchUpdate(telemetry.stream()
                .map(mapper::mapToVehicleTelemetry)
                .collect(Collectors.toList()));
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownType(@Payload Object unknown) {
        log.warn("get unknown payload from orp.telemetry topic\n" + unknown);
    }
}
