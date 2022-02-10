package com.nocmok.orp.telemetry.storage;

import com.nocmok.orp.telemetry.dto.VehicleTelemetry;

import java.util.List;

public interface TelemetryStorage {

    /**
     * Возвращает батч необработанной телеметрии размером не больше указанного.
     * Если запрошено записей больше чем есть в хранилище, то возвращает все записи.
     * Возвращенные записи помечаются обработанными.
     * Вызывать метод только внутри транзакции
     */
    List<VehicleTelemetry> getTelemetryBatch(int batchSize);

    void appendTelemetryBatch(List<VehicleTelemetry> telemetryBatch);
}
