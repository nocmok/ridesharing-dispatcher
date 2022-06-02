package com.nocmok.orp.state_keeper.api;

import java.util.List;

public interface StateKeeper<V extends VehicleState> {

    // TODO Переименовать метод в getActiveVehiclesByIds
    /**
     * Возвращает только активные тс
     */
    List<V> getVehiclesByIds(List<String> ids);

    void updateVehicle(VehicleState vehicle);

    VehicleState createVehicle(VehicleState vehicle);

    List<V> getActiveVehiclesByIdsForUpdate(List<String> ids);
}
