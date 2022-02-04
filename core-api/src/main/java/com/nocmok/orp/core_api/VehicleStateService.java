package com.nocmok.orp.core_api;

import java.util.List;

public interface VehicleService {

    List<Vehicle> getVehiclesByIds(List<String> ids);

    void updateVehicleStateBatch(List<Vehicle> vehicle);
}
