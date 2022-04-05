package com.nocmok.orp.notifier.kafka.orp_telemetry;

import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
import com.nocmok.orp.notifier.kafka.orp_telemetry.mapper.VehicleTelemetryMapper;
import com.nocmok.orp.notifier.service.VehicleGPSStreamingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = "orp.telemetry",
        groupId = "orp_notifier",
        containerFactory = "orpTelemetryKafkaListenerContainerFactory")
@Slf4j
public class OrpTelemetryListener {

    private VehicleGPSStreamingService vehicleGPSStreamingService;
    private VehicleTelemetryMapper vehicleTelemetryMapper;

    @Autowired
    public OrpTelemetryListener(VehicleGPSStreamingService vehicleGPSStreamingService,
                                VehicleTelemetryMapper vehicleTelemetryMapper) {
        this.vehicleGPSStreamingService = vehicleGPSStreamingService;
        this.vehicleTelemetryMapper = vehicleTelemetryMapper;
    }

    @KafkaHandler
    public void receiveVehicleTelemetry(@Payload VehicleTelemetryMessage telemetryMessage) {
        log.info("received " + telemetryMessage);
        vehicleGPSStreamingService.sendGPS(vehicleTelemetryMapper.mapVehicleTelemetryMessageToVehicleGPSMessageServiceDto(telemetryMessage));
    }

    @KafkaHandler(isDefault = true)
    public void fallbackHandler(@Payload Object unknownMessage) {
        log.warn("message with unknown format received, will skip it: " + unknownMessage);
    }
}
