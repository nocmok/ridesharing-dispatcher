package com.nocmok.orp.telemetry.job;

import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.telemetry.service.TelemetryStorageService;
import com.nocmok.orp.telemetry.service.dto.VehicleTelemetry;
import com.nocmok.orp.telemetry.tracker.LatLon;
import com.nocmok.orp.telemetry.tracker.VehicleTrackMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UpdateVehiclePositionJob {

    private TelemetryStorageService telemetryStorageService;
    private VehicleTrackMappingStrategy vehicleTrackMapper;
    private SpatialGraphObjectsStorage graphObjectsStorage;

    /**
     * Длина интервала за который берется батч последней телеметрия
     */
    @Value("${com.nocmok.orp.telemetry.job.timeIntervalToFetchLatestTelemetrySeconds:60}")
    private Integer timeIntervalToFetchLatestTelemetrySeconds;

    @Autowired
    public UpdateVehiclePositionJob(TelemetryStorageService telemetryStorageService, VehicleTrackMappingStrategy vehicleTracker,
                                    SpatialGraphObjectsStorage graphObjectsStorage) {
        this.telemetryStorageService = telemetryStorageService;
        this.vehicleTrackMapper = vehicleTracker;
        this.graphObjectsStorage = graphObjectsStorage;
    }


    /**
     * Забираем телеметрию за последнюю минуту.
     * Группируем телеметрию по машинам.
     * Цикл по машинам:
     * 1) Отправляем телеметрию машины в алгоритм привязки. Получаем последовательность ребер
     * 2) Берем последнее ребро из привязки и объявляем его текущим ребром
     * 3) Обновляем graphObjectStorage
     */
    @Scheduled(fixedDelayString = "${com.nocmok.orp.telemetry.job.UpdateVehiclePositionJob.updateIntervalSeconds:5000}")
    public void updateVehiclePositions() {
        log.debug("start processing telemetry ...");

        var telemetryBySessionId =
                telemetryStorageService.getLatestTelemetryForEachVehiclesAfterTimestamp(Instant.now().minusSeconds(timeIntervalToFetchLatestTelemetrySeconds))
                        .stream()
                        .collect(Collectors.groupingBy(VehicleTelemetry::getSessionId));

        if (telemetryBySessionId.isEmpty()) {
            log.debug("no telemetry to process, skip ...");
            return;
        }

        for (var id : telemetryBySessionId.keySet()) {
            var telemetry = telemetryBySessionId.get(id);
            if (telemetry.isEmpty()) {
                continue;
            }
            var vehicle = graphObjectsStorage.getObject(id);
            if (vehicle.isEmpty()) {
                log.warn("telemetry received for vehicle with id=" + id +
                        ", but no vehicles in objects storage to apply telemetry. Skip telemetry for vehicle with id=" + id);
                continue;
            }

            var matchedTrack = vehicleTrackMapper.matchTrackToGraph(telemetry.stream()
                    .map(t -> new LatLon(t.getLat(), t.getLon()))
                    .collect(Collectors.toList()));

            if (matchedTrack.isEmpty()) {
                log.debug("track mapper returned empty mapping for vehicle with id " + id + ", skip this vehicle");
                continue;
            }

            var currentRoadSegment = matchedTrack.get(matchedTrack.size() - 1);
            var latestTelemetry = telemetry.get(telemetry.size() - 1);

            var updatedVehicle = new ObjectUpdater(vehicle.get());
            updatedVehicle.setLatitude(latestTelemetry.getLat());
            updatedVehicle.setLongitude(latestTelemetry.getLon());
            updatedVehicle.setSegmentStartNodeId(currentRoadSegment.getStartNodeId());
            updatedVehicle.setSegmentEndNodeId(currentRoadSegment.getEndNodeId());

            graphObjectsStorage.updateObject(updatedVehicle);
        }
    }
}
