package com.nocmok.orp.telemetry.service;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStatus;
import com.nocmok.orp.telemetry.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.tracker.VehicleTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Принимает батч телеметрии и делает всю работу по обновлению состояний тс в системе
 */
@Service
@Slf4j
public class VehiclePositionBatchUpdater {

    private final StateKeeper<?> stateKeeper;
    private final VehicleTracker vehicleTracker;

    @Autowired
    public VehiclePositionBatchUpdater(StateKeeper<?> stateKeeper, VehicleTracker vehicleTracker) {
        this.stateKeeper = stateKeeper;
        this.vehicleTracker = vehicleTracker;
    }

    public void batchUpdate(List<VehicleTelemetry> telemetryBatch) {
        var vehicleIdToTrack = telemetryBatch.stream()
                .collect(Collectors.groupingBy(VehicleTelemetry::getSessionId));

        var vehiclesIds = new ArrayList<>(vehicleIdToTrack.keySet());
        var vehicles = stateKeeper.getVehiclesByIds(vehiclesIds);
        var vehiclesToUpdate = new ArrayList<VehicleProjection>();

        for (var vehicle : vehicles) {
            var gpsTrack = new ArrayList<>(List.of(vehicle.getGCS()));
            gpsTrack.addAll(vehicleIdToTrack.get(vehicle.getId()).stream()
                    .map(telemetry -> new GCS(telemetry.getLat(), telemetry.getLon()))
                    .collect(Collectors.toList()));

            var roadLog = vehicleTracker.matchTrackToGraph(gpsTrack);

            if (roadLog.isEmpty()) {
                continue;
            }

            // Для последнего ребра в логе вычисляем привязку
            var roadBinding = vehicleTracker.getBinding(roadLog.get(roadLog.size() - 1),
                    new GCS(gpsTrack.get(gpsTrack.size() - 1).lat(), gpsTrack.get(gpsTrack.size() - 1).lon()));

            var vehicleProjection = new VehicleProjection(vehicle);
            vehicleProjection.setGcs(gpsTrack.get(gpsTrack.size() - 1));
            vehicleProjection.setGraphBinding(roadBinding);

            vehiclesToUpdate.add(vehicleProjection);
        }

        stateKeeper.updateVehiclesBatch(vehiclesToUpdate);
    }


    /**
     * Реализация интерфейса Vehicle,
     * которая хранит только часть полей, связанную с позиционированием тс
     */
    private static class VehicleProjection implements Vehicle {
        private String sessionId;
        private GraphBinding graphBinding;
        private GCS gcs;

        public VehicleProjection() {
        }

        public VehicleProjection(Vehicle vehicle) {
            this.sessionId = vehicle.getId();
            this.graphBinding = vehicle.getRoadBinding();
            this.gcs = vehicle.getGCS();
        }

        @Override public String getId() {
            return sessionId;
        }

        @Override public VehicleStatus getStatus() {
            return null;
        }

        @Override public void setStatus(VehicleStatus status) {
            throw new UnsupportedOperationException("illegal update call on stub object");
        }

        @Override public List<ScheduleNode> getSchedule() {
            return null;
        }

        @Override public void setSchedule(List<ScheduleNode> schedule) {
            throw new UnsupportedOperationException("illegal update call on stub object");
        }

        @Override public Integer getCapacity() {
            return null;
        }

        @Override public Integer getResidualCapacity() {
            return null;
        }

        @Override public void setResidualCapacity(int capacity) {
            throw new UnsupportedOperationException("illegal update call on stub object");
        }

        @Override public GCS getGCS() {
            return gcs;
        }

        @Override public GraphBinding getRoadBinding() {
            return graphBinding;
        }

        @Override public List<GraphNode> getRouteScheduled() {
            return null;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public void setGraphBinding(GraphBinding graphBinding) {
            this.graphBinding = graphBinding;
        }

        public void setGcs(GCS gcs) {
            this.gcs = gcs;
        }
    }
}
