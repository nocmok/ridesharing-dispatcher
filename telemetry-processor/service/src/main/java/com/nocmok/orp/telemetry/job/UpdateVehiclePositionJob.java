package com.nocmok.orp.telemetry.job;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStatus;
import com.nocmok.orp.telemetry.service.TelemetryStorageService;
import com.nocmok.orp.telemetry.service.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.tracker.VehicleTracker;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UpdateVehiclePositionJob {

    private TelemetryStorageService telemetryStorageService;
    private VehicleTracker vehicleTracker;
    private StateKeeper<?> stateKeeper;

    @Autowired
    public UpdateVehiclePositionJob(TelemetryStorageService telemetryStorageService, VehicleTracker vehicleTracker,
                                    StateKeeper<?> stateKeeper) {
        this.telemetryStorageService = telemetryStorageService;
        this.vehicleTracker = vehicleTracker;
        this.stateKeeper = stateKeeper;
    }

    @Scheduled(fixedDelayString = "5000")
    public void updateVehiclePositions() {
        log.info("start processing telemetry ...");

        var telemetryBySessionId = telemetryStorageService.getLatestTelemetryForEachVehiclesAfterTimestamp(Instant.now().minusSeconds(60)).stream()
                .collect(Collectors.groupingBy(VehicleTelemetry::getSessionId));

        if (telemetryBySessionId.isEmpty()) {
            log.info("no telemetry to process, skip ...");
            return;
        }

        var vehicles = stateKeeper.getVehiclesByIds(new ArrayList<>(telemetryBySessionId.keySet())).stream()
                .collect(Collectors.toMap(Vehicle::getId, Function.identity()));

        if(vehicles.isEmpty()) {
            log.warn("telemetry received, but no vehicles in state keeper to apply telemetry, skip ...");
        }

        var vehiclesToUpdate = new ArrayList<Vehicle>();

        for (var id : telemetryBySessionId.keySet()) {
            var telemetry = telemetryBySessionId.get(id);
            if (telemetry.isEmpty()) {
                continue;
            }
            var vehicle = vehicles.get(id);
            var matchedTrack = vehicleTracker.matchTrackToGraph(telemetry.stream()
                    .map(t -> new GCS(t.getLat(), t.getLon()))
                    .collect(Collectors.toList()));

            var currentRoad = matchedTrack.isEmpty()
                    ? Optional.ofNullable(vehicle.getRoadBinding()).map(GraphBinding::getRoad)
                    : Optional.of(matchedTrack.get(matchedTrack.size() - 1));

            var currentRoadBinding = currentRoad
                    .map(road -> vehicleTracker.getBinding(road,
                            new GCS(telemetry.get(telemetry.size() - 1).getLat(), telemetry.get(telemetry.size() - 1).getLon())))
                    .orElse(vehicle.getRoadBinding());

            vehiclesToUpdate.add(VehicleProjection.builder()
                    .id(vehicle.getId())
                    .graphBinding(currentRoadBinding)
                    .routeScheduled(getUpdatedVehicleRoute(vehicle, matchedTrack))
                    .build());
        }

        stateKeeper.updateVehiclesBatch(vehiclesToUpdate);

        log.info("updated states for " + vehiclesToUpdate.size() + " vehicles");
        log.info("telemetry processed");
    }

    private List<GraphRoad> getRouteRoadsByNodeSequence(List<GraphNode> nodes) {
        var routeRoads = new ArrayList<GraphRoad>();
        for (int i = 1; i < nodes.size(); ++i) {
            routeRoads.add(new GraphRoad(nodes.get(i - 1), nodes.get(i)));
        }
        return routeRoads;
    }

    private List<GraphNode> getNodeSequenceByRouteRoads(List<GraphRoad> roads) {
        if (roads.isEmpty()) {
            return Collections.emptyList();
        }
        var nodes = new ArrayList<GraphNode>();
        nodes.add(roads.get(0).getStartNode());
        nodes.addAll(roads.stream().map(GraphRoad::getEndNode).collect(Collectors.toList()));
        return nodes;
    }

    private List<GraphNode> getUpdatedVehicleRoute(Vehicle vehicle, List<GraphRoad> latestTrack) {
        if (vehicle.getSchedule().isEmpty()) {
            log.warn("vehicle without schedule appeared to compute route");
            return vehicle.getRouteScheduled();
        }
        if (vehicle.getRouteScheduled().size() < 2) {
            return vehicle.getRouteScheduled();
        }
        if (latestTrack.size() < 1) {
            return vehicle.getRouteScheduled();
        }
        var routeRoads = getRouteRoadsByNodeSequence(vehicle.getRouteScheduled());
        int roadsPassed = routeRoads.indexOf(latestTrack.get(latestTrack.size() - 1));

        log.info("route roads: " + routeRoads);
        log.info("current road: " + latestTrack.get(latestTrack.size() - 1));
        log.info("roads passed: " + roadsPassed);

        if (roadsPassed == -1) {
            return computeRoute(latestTrack.get(latestTrack.size() - 1).getEndNode().getNodeId(), vehicle.getSchedule().get(0).getNodeId());
        }
        var updatedRouteRoads = routeRoads.subList(roadsPassed, routeRoads.size());

        return getNodeSequenceByRouteRoads(updatedRouteRoads);
    }

    private List<GraphNode> computeRoute(Integer startNodeId, Integer endNodeId) {
        log.info("road recomputing initiated, but not implemented");
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Реализация интерфейса Vehicle,
     * которая хранит только часть полей, связанную с позиционированием тс
     */
    @Builder
    private static class VehicleProjection implements Vehicle {
        private String id;
        private GraphBinding graphBinding;
        private List<GraphNode> routeScheduled;

        public VehicleProjection(String id, GraphBinding graphBinding, List<GraphNode> routeScheduled) {
            this.id = id;
            this.graphBinding = graphBinding;
            this.routeScheduled = routeScheduled;
        }

        @Override public String getId() {
            return id;
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

        @Override public GraphBinding getRoadBinding() {
            return graphBinding;
        }

        @Override public List<GraphNode> getRouteScheduled() {
            return routeScheduled;
        }
    }
}
