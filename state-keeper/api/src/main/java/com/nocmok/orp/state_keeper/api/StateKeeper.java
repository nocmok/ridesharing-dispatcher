package com.nocmok.orp.state_keeper.api;

import java.util.List;

public interface StateKeeper<V extends VehicleState> {

    List<String> getActiveVehiclesIds();

    List<V> getActiveVehicles();

    List<V> getVehiclesByIds(List<String> ids);

    void updateVehiclesBatch(List<? extends VehicleState> vehicle);
}
