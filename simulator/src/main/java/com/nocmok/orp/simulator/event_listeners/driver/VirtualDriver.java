package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.RequestAssignConfirmationEvent;
import com.nocmok.orp.simulator.event_bus.event.ServiceRequestEvent;
import com.nocmok.orp.simulator.event_bus.event.TicTacEvent;
import com.nocmok.orp.simulator.service.api.DriverApi;
import com.nocmok.orp.simulator.service.api.ServiceRequestConfirmation;
import com.nocmok.orp.simulator.service.telemetry.FollowScheduleWalk;
import com.nocmok.orp.simulator.service.telemetry.NoWalk;
import com.nocmok.orp.simulator.service.telemetry.TelemetrySender;
import com.nocmok.orp.simulator.service.telemetry.WalkStrategy;
import com.nocmok.orp.solver.api.RouteNode;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class VirtualDriver {

    private String sessionId;
    private EventBus eventBus;
    private TelemetrySender telemetrySender;

    // volatile чтобы при обновлении стратегии изменение было видно в других потоках
    private volatile WalkStrategy walkStrategy;

    private SpatialGraphMetadataStorage graphMetadataStorage;
    private SpatialGraphObjectsStorage graphObjectsStorage;
    private DriverApi driverApi;

    private double currentLatitude;
    private double currentLongitude;

    public VirtualDriver(String sessionId, EventBus eventBus, TelemetrySender telemetrySender, SpatialGraphMetadataStorage graphMetadataStorage,
                         SpatialGraphObjectsStorage graphObjectsStorage, DriverApi driverApi) {
        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.telemetrySender = telemetrySender;
        this.graphMetadataStorage = graphMetadataStorage;
        this.graphObjectsStorage = graphObjectsStorage;
        this.driverApi = driverApi;

        var sessionOnGraph = graphObjectsStorage.getObject(sessionId);
        if (sessionOnGraph.isEmpty()) {
            throw new RuntimeException("no entry in object storage for session with id " + sessionId);
        }

        this.currentLatitude = sessionOnGraph.get().getLatitude();
        this.currentLongitude = sessionOnGraph.get().getLongitude();

        // TODO
        // Сделать инициализацию стратегии в зависимости от текущего состояния.
        // Например если у тс есть непустой план, то проинициализировать FollowScheduleWalk стратегию
        this.walkStrategy = new NoWalk(sessionId, currentLatitude, currentLongitude);

        registerCallbacks();
    }

    private void registerCallbacks() {
        eventBus.subscribe(TicTacEvent.class, this::onTimePassed);
        eventBus.subscribe(ServiceRequestEvent.class, this::onServiceRequest);
        eventBus.subscribe(RequestAssignConfirmationEvent.class, this::onRequestAssigningConfirmation);
    }

    private void onTimePassed(TicTacEvent event) {
        var telemetry = this.walkStrategy.nextTelemetry(event.getMilliseconds() / 1000d);
        this.currentLatitude = telemetry.getLatitude();
        this.currentLongitude = telemetry.getLongitude();
        telemetrySender.sendTelemetry(telemetry);
    }

    private void onServiceRequest(ServiceRequestEvent event) {
        log.info("received service request, send confirmation ...");
        // отправить подтверждение
        driverApi.confirmRequest(ServiceRequestConfirmation.builder()
                .sessionId(event.getSessionId())
                .requestId(event.getRequestId())
                .reservationId(event.getReservationId())
                .build());
        log.info("confirmation sent ...");
    }

    private void onRequestAssigningConfirmation(RequestAssignConfirmationEvent event) {
        // Обновить маршрут в генераторе телеметрии
        if (event.getRouteScheduled().size() < 2) {
            log.warn("received invalid route in request assignment confirmation message");
            return;
        }
        var routeNodeIds = event.getRouteScheduled().stream()
                .map(RouteNode::getNodeId)
                .collect(Collectors.toUnmodifiableList());
        var segmentRoute = graphMetadataStorage.getSegments(
                routeNodeIds.subList(0, routeNodeIds.size() - 1),
                routeNodeIds.subList(1, routeNodeIds.size())
        );
        log.info("received request assignment confirmation, switch walk strategy");
        this.walkStrategy = new FollowScheduleWalk(sessionId, segmentRoute, currentLatitude, currentLongitude);
    }
}
