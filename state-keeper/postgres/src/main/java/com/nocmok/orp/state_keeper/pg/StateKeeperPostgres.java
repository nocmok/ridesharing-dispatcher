package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.core_api.StateKeeper;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class StateKeeperPostgres implements StateKeeper<Vehicle> {

    private final VehicleStateRepository vehicleStateRepository;

    public StateKeeperPostgres(DataSource dataSource, ObjectMapper objectMapper) {
        this.vehicleStateRepository = new VehicleStateRepository(dataSource, objectMapper);
    }

    @Override public List<String> getActiveVehiclesIds() {
        return vehicleStateRepository.getActiveVehiclesIds();
    }

    @Override public List<Vehicle> getActiveVehicles() {
        return vehicleStateRepository.getActiveVehicles();
    }

    @Override public List<Vehicle> getVehiclesByIds(List<String> ids) {
        return vehicleStateRepository.getVehiclesByIds(ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }

    @Override public void updateVehiclesBatch(List<? extends com.nocmok.orp.core_api.Vehicle> vehicle) {
        vehicleStateRepository.updateVehiclesBatch(vehicle);
    }
}
