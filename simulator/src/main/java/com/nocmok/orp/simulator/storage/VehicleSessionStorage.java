package com.nocmok.orp.simulator.storage;

import com.nocmok.orp.simulator.storage.dto.VehicleSession;

import java.time.Instant;
import java.util.List;

public interface VehicleSessionStorage {

    List<VehicleSession> readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(Instant timestamp);
}
