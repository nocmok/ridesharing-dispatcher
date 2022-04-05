package com.nocmok.orp.notifier.kafka.orp_telemetry.mapper;

import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
import com.nocmok.orp.notifier.service.dto.VehicleGPSMessageServiceDto;
import org.springframework.stereotype.Component;

@Component
public class VehicleTelemetryMapper {

    public VehicleGPSMessageServiceDto mapVehicleTelemetryMessageToVehicleGPSMessageServiceDto(VehicleTelemetryMessage vehicleTelemetryMessage) {
        return VehicleGPSMessageServiceDto.builder()
                .sessionId(vehicleTelemetryMessage.getSessionId())
                .latitude(vehicleTelemetryMessage.getLat())
                .longitude(vehicleTelemetryMessage.getLon())
                .recordedAt(vehicleTelemetryMessage.getRecordedAt())
                .build();
    }
}
