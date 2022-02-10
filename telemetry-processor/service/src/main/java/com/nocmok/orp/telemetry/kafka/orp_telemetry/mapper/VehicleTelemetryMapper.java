package com.nocmok.orp.telemetry.kafka.orp_telemetry.mapper;

import com.nocmok.orp.telemetry.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetryMessage;

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
