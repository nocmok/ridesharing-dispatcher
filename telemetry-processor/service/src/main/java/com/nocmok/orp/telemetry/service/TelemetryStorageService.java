package com.nocmok.orp.telemetry.service;

import com.nocmok.orp.telemetry.service.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.storage.TelemetryStorage;
import com.nocmok.orp.telemetry.storage.dto.VehicleTelemetryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TelemetryStorageService {

    private TelemetryStorage telemetryStorage;

    @Autowired
    public TelemetryStorageService(TelemetryStorage telemetryStorage) {
        this.telemetryStorage = telemetryStorage;
    }

    private VehicleTelemetryRecord mapVehicleTelemetryToVehicleTelemetryStorageRecord(VehicleTelemetry vehicleTelemetry) {
        return VehicleTelemetryRecord.builder()
                .sessionId(vehicleTelemetry.getSessionId())
                .latitude(vehicleTelemetry.getLat())
                .longitude(vehicleTelemetry.getLon())
                .accuracy(vehicleTelemetry.getAccuracy())
                .recordedAt(vehicleTelemetry.getRecordedAt())
                .build();
    }

    private VehicleTelemetry mapVehicleTelemetryStorageRecordToVehicleTelemetry(VehicleTelemetryRecord vehicleTelemetryRecord) {
        return VehicleTelemetry.builder()
                .sessionId(vehicleTelemetryRecord.getSessionId())
                .lat(vehicleTelemetryRecord.getLatitude())
                .lon(vehicleTelemetryRecord.getLongitude())
                .accuracy(vehicleTelemetryRecord.getAccuracy())
                .recordedAt(vehicleTelemetryRecord.getRecordedAt())
                .build();
    }

    public void storeTelemetry(List<VehicleTelemetry> telemetryBatch) {
        telemetryStorage.appendTelemetryBatch(telemetryBatch.stream()
                .map(this::mapVehicleTelemetryToVehicleTelemetryStorageRecord)
                .collect(Collectors.toList()));
    }

    public List<VehicleTelemetry> getLatestTelemetryForEachVehiclesAfterTimestamp(Instant timestamp) {
        return telemetryStorage.getLatestRecordsForEachVehicleAfterTimestamp(timestamp).stream()
                .map(this::mapVehicleTelemetryStorageRecordToVehicleTelemetry)
                .collect(Collectors.toList());
    }
}
