package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.tools.EarthMath;
import com.nocmok.orp.kafka.orp_input.OrderStatus;
import com.nocmok.orp.simulator.service.api.DriverApi;
import com.nocmok.orp.simulator.service.api.UpdateOrderStatusRequest;
import com.nocmok.orp.state_keeper.api.ScheduleEntry;
import com.nocmok.orp.state_keeper.api.ScheduleEntryKind;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DefaultScheduleExecutor implements ScheduleExecutor {

    private final DriverApi driverApi;
    private String sessionId;
    private Deque<Segment> routeToFollow;
    private Deque<ScheduleEntry> scheduleToExecute;
    private double progressOnCurrentSegment;
    private long lastRecordedTimeMillis;

    public DefaultScheduleExecutor(String sessionId, List<ScheduleEntry> scheduleToExecute, List<Segment> routeToFollow, Double currentLatitude,
                                   Double currentLongitude, DriverApi driverApi) {
        if (routeToFollow.isEmpty()) {
            throw new IllegalArgumentException("route should not be empty");
        }

        if (currentLatitude == null) {
            currentLatitude = routeToFollow.get(0).getStartNode().getLatitude();
            currentLongitude = routeToFollow.get(0).getStartNode().getLongitude();
        }

        this.sessionId = sessionId;
        this.scheduleToExecute = new ArrayDeque<>(scheduleToExecute);
        this.routeToFollow = new ArrayDeque<>(routeToFollow);
        this.driverApi = driverApi;

        this.progressOnCurrentSegment =
                this.routeToFollow.getFirst().getCost() * getRelativeProgressOnRoadSegment(currentLatitude, currentLongitude, this.routeToFollow.getFirst());

        this.lastRecordedTimeMillis = System.currentTimeMillis();

        log.info("will execute schedule " + this.scheduleToExecute);
    }

    // Относительная степень прохождения дороги. Число от 0 до 1
    private double getRelativeProgressOnRoadSegment(double latitude, double longitude, Segment segment) {
        double distanceToPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getStartNode().getLatitude(), segment.getStartNode().getLongitude());
        double distanceFromPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getEndNode().getLatitude(), segment.getEndNode().getLongitude());
        return distanceToPoint / (distanceToPoint + distanceFromPoint);
    }

    private boolean segmentContainsNode(Segment segment, String nodeId) {
        return Objects.equals(segment.getStartNode().getId(), nodeId) ||
                Objects.equals(segment.getEndNode().getId(), nodeId);
    }

    private void handleRoadPassed(Segment road) {
        log.info("road passed " + road);
        while (!scheduleToExecute.isEmpty() && segmentContainsNode(road, scheduleToExecute.getFirst().getNodeId())) {

            var nextCheckpoint = scheduleToExecute.pollFirst();

            if (nextCheckpoint == null) {
                log.warn("found null entry in schedule, skip it ...");
                continue;
            }

            if (ScheduleEntryKind.PICKUP == nextCheckpoint.getKind()) {

                log.info("checkpoint passed " + nextCheckpoint);

                driverApi.updateOrderStatus(UpdateOrderStatusRequest.builder()
                        .sessionId(sessionId)
                        .orderId(nextCheckpoint.getOrderId())
                        .updatedStatus(OrderStatus.SERVING)
                        .build());
            } else if (ScheduleEntryKind.DROPOFF == nextCheckpoint.getKind()) {

                log.info("checkpoint passed " + nextCheckpoint);

                driverApi.updateOrderStatus(UpdateOrderStatusRequest.builder()
                        .sessionId(sessionId)
                        .orderId(nextCheckpoint.getOrderId())
                        .updatedStatus(OrderStatus.SERVED)
                        .build());
            } else {
                throw new RuntimeException("unknown schedule entry kind " + nextCheckpoint.getKind());
            }
        }
    }

    private double skipAllPassedRoads(double time) {
        while (!routeToFollow.isEmpty() && progressOnCurrentSegment + time > routeToFollow.getFirst().getCost()) {
            time -= routeToFollow.getFirst().getCost() - progressOnCurrentSegment;
            handleRoadPassed(routeToFollow.pollFirst());
            progressOnCurrentSegment = 0;
        }
        return time;
    }

    @Override public void tryExecuteSchedule(double time) {
        long now = System.currentTimeMillis();
        time = skipAllPassedRoads((now - lastRecordedTimeMillis) / 1000d);
        progressOnCurrentSegment += time;
        this.lastRecordedTimeMillis = now;
    }
}
