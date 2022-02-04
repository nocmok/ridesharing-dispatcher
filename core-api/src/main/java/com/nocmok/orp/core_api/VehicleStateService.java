package com.nocmok.orp.core_api;

import java.util.List;

public interface VehicleStateService<V extends Vehicle> {

    List<V> getVehiclesByIds(List<String> ids);

    void updateVehiclesBatch(List<V> vehicle);
}
