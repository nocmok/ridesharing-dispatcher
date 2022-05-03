package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.Segment;
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
import com.nocmok.orp.simulator.storage.VehicleSessionStorage;
import com.nocmok.orp.solver.api.RouteNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class VirtualDriver {

    private String sessionId;
    private EventBus eventBus;
    private TelemetrySender telemetrySender;

    // volatile чтобы при обновлении стратегии изменение было видно в других потоках
    private volatile WalkStrategy walkStrategy;
    private volatile ScheduleExecutor scheduleExecutor;
    private volatile CurrentRoadTracker currentRoadTracker;

    private SpatialGraphMetadataStorage graphMetadataStorage;
    private SpatialGraphObjectsStorage graphObjectsStorage;
    private DriverApi driverApi;
    private VehicleSessionStorage vehicleSessionStorage;

    private double currentLatitude;
    private double currentLongitude;
    private Segment currentRoadSegment;

    public VirtualDriver(String sessionId, EventBus eventBus, TelemetrySender telemetrySender, SpatialGraphMetadataStorage graphMetadataStorage,
                         SpatialGraphObjectsStorage graphObjectsStorage, DriverApi driverApi, VehicleSessionStorage vehicleSessionStorage) {

        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.telemetrySender = telemetrySender;
        this.graphMetadataStorage = graphMetadataStorage;
        this.graphObjectsStorage = graphObjectsStorage;
        this.driverApi = driverApi;
        this.vehicleSessionStorage = vehicleSessionStorage;

        var sessionOnGraph = graphObjectsStorage.getObject(sessionId);
        if (sessionOnGraph.isEmpty()) {
            throw new RuntimeException("no entry in object storage for session with id " + sessionId);
        }

        var sessionDetails = vehicleSessionStorage.getSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("no entry in session storage for session with id " + sessionId));

        this.currentLatitude = sessionOnGraph.get().getLatitude();
        this.currentLongitude = sessionOnGraph.get().getLongitude();
        this.currentRoadSegment = sessionOnGraph.get().getSegment();

        // TODO
        // Сделать инициализацию стратегии в зависимости от текущего состояния.
        // Например если у тс есть непустой план, то проинициализировать FollowScheduleWalk стратегию
        if (sessionDetails.getSchedule().isEmpty()) {
            this.walkStrategy = new NoWalk(sessionId, currentLatitude, currentLongitude);
        } else {
            throw new UnsupportedOperationException("not implemented");
        }

        if (sessionDetails.getSchedule().isEmpty()) {
            this.scheduleExecutor = IdleScheduleExecutor.instance();
        } else {
            // TODO
            throw new UnsupportedOperationException("not implemented");
        }

        this.currentRoadTracker = new IdleCurrentRoadTracker(this.currentRoadSegment);

        registerCallbacks();
    }

    private void registerCallbacks() {
        eventBus.subscribe(TicTacEvent.class, this::onTimePassed);
        eventBus.subscribe(ServiceRequestEvent.class, sessionId, this::onServiceRequest);
        eventBus.subscribe(RequestAssignConfirmationEvent.class, sessionId, this::onRequestAssigningConfirmation);
    }

    private void onTimePassed(TicTacEvent event) {
        var telemetry = this.walkStrategy.nextTelemetry(event.getMilliseconds() / 1000d);
        this.currentLatitude = telemetry.getLatitude();
        this.currentLongitude = telemetry.getLongitude();

        currentRoadTracker.updateCurrentRoad();
        scheduleExecutor.tryExecuteSchedule(event.getMilliseconds() / 1000d);

        telemetrySender.sendTelemetry(telemetry);
    }

    private void onServiceRequest(ServiceRequestEvent event) {
        if (!Objects.equals(sessionId, event.getSessionId())) {
            log.warn("received event with invalid session id. skip ...");
            return;
        }
        log.info("received service request " + event + ", send confirmation ...");
        var currentScheduleExecutor = this.scheduleExecutor;
        try {
            // приостанавливаем подтверждения контрольных точек пока не придет обновленный план

            this.scheduleExecutor = IdleScheduleExecutor.instance();
            // отправить подтверждение
            driverApi.confirmRequest(ServiceRequestConfirmation.builder()
                    .sessionId(event.getSessionId())
                    .requestId(event.getRequestId())
                    .reservationId(event.getReservationId())
                    .build());

            log.info("confirmation sent ...");
        } catch (Exception e) {
            // не смогли отправить подтверждение, возобновляем отправку подтверждений контрольных точек
            this.scheduleExecutor = currentScheduleExecutor;
            log.info("failed to sent confirmation");
        }
    }

    private void onRequestAssigningConfirmation(RequestAssignConfirmationEvent event) {
        if (!Objects.equals(sessionId, event.getSessionId())) {
            log.warn("received event with invalid session id. skip ...");
            return;
        }
        // Обновить маршрут в генераторе телеметрии
        if (event.getRouteScheduled().size() < 2) {
            log.warn("received invalid route in request assignment confirmation message");
            return;
        }
        var routeNodeIds = event.getRouteScheduled().stream()
                .map(RouteNode::getNodeId)
                .collect(Collectors.toUnmodifiableList());

        List<Segment> segmentRoute;

        if (routeNodeIds.stream().anyMatch(
                id -> !Objects.equals(id, currentRoadSegment.getStartNode().getId()) && !Objects.equals(id, currentRoadSegment.getEndNode().getId()))) {

            segmentRoute = graphMetadataStorage.getSegments(
                    routeNodeIds.subList(0, routeNodeIds.size() - 1),
                    routeNodeIds.subList(1, routeNodeIds.size()));
        } else {
            segmentRoute = List.of(currentRoadSegment);
        }

        var schedule = event.getSchedule();

        log.info("received request assignment confirmation " + event);


        // TODO
        // Проблема
        // Приходит маршрут, который отстает на несколько секунд от нормального.
        // Из-за этого визуально машина возвращается назад

        // Варианты решения
        // 1) Стопать вообще генерацию телеметрии, пока не придет подтверждение
        // 2) Брать текущее ребро через metadata storage. Затем отматывать пришедший маршрут до текущего ребра
        // 3) Самому считать ближайшее к текущей точке ребро из пришедшего маршрута и отматывать до него
        // 4) Брать текущее ребро из текущего walk strategy и отматывать до него
        // 5) Сделать отдельно компонент который отслеживает текущее ребро
        //      (По идее нужна фабрика, чтобы отдавать согласованные стратегии генерации телеметрии и отслеживания текущего ребра)

//        var currentRoad = currentRoadTracker.getCurrentRoad();
//        if (segmentRoute.contains(currentRoad)) {
//            var trimmedRoute = segmentRoute.subList(segmentRoute.indexOf(currentRoad), segmentRoute.size());
//            log.info("received route " + segmentRoute);
//            log.info("trimmed route " + trimmedRoute);
//            this.currentRoadTracker = new DefaultCurrentRoadTracker(trimmedRoute, currentLatitude, currentLongitude);
//            this.walkStrategy = new FollowScheduleWalk(sessionId, trimmedRoute, currentLatitude, currentLongitude);
//            this.scheduleExecutor = new DefaultScheduleExecutor(sessionId, schedule, trimmedRoute, currentLatitude, currentLongitude, driverApi);
//        } else {
        this.currentRoadTracker = new DefaultCurrentRoadTracker(segmentRoute, currentLatitude, currentLongitude);
        this.walkStrategy = new FollowScheduleWalk(sessionId, segmentRoute, currentLatitude, currentLongitude);
        this.scheduleExecutor = new DefaultScheduleExecutor(sessionId, schedule, segmentRoute, currentLatitude, currentLongitude, driverApi);
//        }
    }
}
