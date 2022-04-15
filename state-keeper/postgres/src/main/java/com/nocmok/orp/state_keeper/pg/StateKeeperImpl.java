package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class StateKeeperImpl implements StateKeeper<VehicleDto> {

    private final VehicleStateRepository vehicleStateRepository;

    public StateKeeperImpl(DataSource dataSource, ObjectMapper objectMapper) {
        this.vehicleStateRepository = new VehicleStateRepository(dataSource, objectMapper);
    }

    @Override public List<String> getActiveVehiclesIds() {
        return vehicleStateRepository.getActiveVehiclesIds();
    }

    @Override public List<VehicleDto> getActiveVehicles() {
        return vehicleStateRepository.getActiveVehicles();
    }

    @Override public List<VehicleDto> getVehiclesByIds(List<String> ids) {
        return vehicleStateRepository.getVehiclesByIds(ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }

    @Override public void updateVehiclesBatch(List<? extends VehicleState> vehicle) {
        vehicleStateRepository.updateVehiclesBatch(vehicle);
    }

    @Override public VehicleState createVehicle(VehicleState vehicle) {
        return vehicleStateRepository.createVehicle(vehicle);
    }
}
