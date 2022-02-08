package com.nocmok.orp.telemetry.service;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStatus;
import com.nocmok.orp.telemetry.kafka.orp_telemetry.dto.VehicleTelemetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateActuator {

    private final VehiclePositioner vehiclePositioner;
    private final StateKeeper<?> stateKeeper;

    @Autowired
    public StateActuator(VehiclePositioner vehiclePositioner, StateKeeper<?> stateKeeper) {
        this.vehiclePositioner = vehiclePositioner;
        this.stateKeeper = stateKeeper;
    }

    public void updateStateBatch(List<VehicleTelemetry> telemetryBatch) {
        // 1. Сгруппировать телеметрию по session_id
        // 2. Выбрать в каждой группе самую позднюю запись
        // 3. Передать записи в байндер
        // 4. Обернуть привязки в объекты vehicle
        // 5. сделать updateBatch через state-keeper

        var sessionIdToTelemetryList = telemetryBatch.stream()
                .collect(Collectors.groupingBy(VehicleTelemetry::getSessionId));

        var latestTelemetries = new ArrayList<VehicleTelemetry>();

        for (var sessionIdTelemetry : sessionIdToTelemetryList.entrySet()) {
            var latestTelemetry = sessionIdTelemetry.getValue().stream()
                    .max(Comparator.<VehicleTelemetry>comparingLong(vt -> vt.getRecordedAt().toEpochMilli()));

            if (latestTelemetry.isEmpty()) {
                continue;
            }

            latestTelemetries.add(latestTelemetry.get());
        }

        var vehiclesToUpdate = latestTelemetries.stream()
                .map(telemetry -> new VehicleStub(telemetry.getSessionId(), vehiclePositioner.bindVehicle(telemetry)))
                .collect(Collectors.toList());

        stateKeeper.updateVehiclesBatch(vehiclesToUpdate);
    }

    private static class VehicleStub implements Vehicle {
        private final String sessionId;
        private final GraphBinding graphBinding;

        public VehicleStub(String sessionId, GraphBinding graphBinding) {
            this.sessionId = sessionId;
            this.graphBinding = graphBinding;
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
            return null;
        }

        @Override public GraphBinding getRoadBinding() {
            return graphBinding;
        }

        @Override public Double getDistanceScheduled() {
            return null;
        }
    }
}
