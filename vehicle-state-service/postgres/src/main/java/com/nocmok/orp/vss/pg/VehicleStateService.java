package com.nocmok.orp.vss.pg;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleStateService implements com.nocmok.orp.core_api.VehicleStateService<Vehicle> {

    private final VehicleStateRepository vehicleStateRepository;

    public VehicleStateService(DataSource dataSource) {
        this.vehicleStateRepository = new VehicleStateRepository(dataSource);
    }

    @Override public List<Vehicle> getVehiclesByIds(List<String> ids) {
        return vehicleStateRepository.getVehiclesByIds(ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }

    @Override public void updateVehiclesBatch(List<Vehicle> vehicle) {
        vehicleStateRepository.updateVehiclesBatch(vehicle);
    }
}
