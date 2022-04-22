package com.nocmok.orp.state_keeper.api;

import java.util.List;

public interface StateKeeper<V extends VehicleState> {

    List<String> getActiveVehiclesIds();

    List<V> getActiveVehicles();

    // TODO Переименовать метод в getActiveVehiclesByIds
    /**
     * Возвращает только активные тс
     */
    List<V> getVehiclesByIds(List<String> ids);

    void updateVehiclesBatch(List<? extends VehicleState> vehicle);

    VehicleState createVehicle(VehicleState vehicle);

    List<V> getActiveVehiclesByIdsForUpdate(List<String> ids);
}
