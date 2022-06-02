package com.nocmok.orp.simulator.storage;

import com.nocmok.orp.simulator.storage.dto.VehicleSession;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VehicleSessionStorage {

    List<VehicleSession> readActiveVehiclesCreatedAfterTimestampOrderedByCreationTime(Instant timestamp);

    Optional<VehicleSession> getSessionById(String sessionId);
}
