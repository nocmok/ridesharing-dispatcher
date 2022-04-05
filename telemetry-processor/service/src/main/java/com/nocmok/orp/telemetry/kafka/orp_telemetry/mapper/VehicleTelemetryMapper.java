package com.nocmok.orp.telemetry.kafka.orp_telemetry.mapper;

import com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage;
import com.nocmok.orp.telemetry.service.dto.VehicleTelemetry;
import org.springframework.stereotype.Component;

@Component
public class VehicleTelemetryMapper {

    public VehicleTelemetry mapToVehicleTelemetry(VehicleTelemetryMessage message) {
        return new VehicleTelemetry(
                message.getSessionId(),
                message.getLat(),
                message.getLon(),
                message.getAccuracy(),
                message.getRecordedAt());
    }
}
