package com.nocmok.orp.state_keeper.pg;

import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateKeeperImpl implements StateKeeper<VehicleDto> {

    private final VehicleStateRepository vehicleStateRepository;

    @Autowired
    public StateKeeperImpl(VehicleStateRepository vehicleStateRepository) {
        this.vehicleStateRepository = vehicleStateRepository;
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

    @Override public List<VehicleDto> getActiveVehiclesByIdsForUpdate(List<String> ids) {
        return vehicleStateRepository.getVehiclesByIdsForUpdate(ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }
}
