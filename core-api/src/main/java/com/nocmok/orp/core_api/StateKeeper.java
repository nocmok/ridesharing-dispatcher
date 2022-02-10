package com.nocmok.orp.core_api;

import java.util.List;

public interface StateKeeper<V extends Vehicle> {

    List<String> getActiveVehiclesIds();

    List<V> getActiveVehicles();

    List<V> getVehiclesByIds(List<String> ids);

    void updateVehiclesBatch(List<? extends Vehicle> vehicle);
}
