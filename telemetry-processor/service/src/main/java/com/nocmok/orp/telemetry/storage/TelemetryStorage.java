package com.nocmok.orp.telemetry.storage;

import com.nocmok.orp.telemetry.storage.dto.VehicleTelemetryRecord;

import java.time.Instant;
import java.util.List;

public interface TelemetryStorage {

    void appendTelemetryBatch(List<VehicleTelemetryRecord> telemetryBatch);

    /**
     * Возвращает всю телеметрию по всем тс начиная с указанного времени отсортированную по времени записи
     */
    List<VehicleTelemetryRecord> getLatestRecordsForEachVehicleAfterTimestamp(Instant timestamp);
}
