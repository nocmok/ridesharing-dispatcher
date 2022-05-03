package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.tools.EarthMath;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Slf4j
public class DefaultCurrentRoadTracker implements CurrentRoadTracker {

    private Deque<Segment> routeToFollow;
    private double progressOnCurrentSegment;
    private long lastRecordedTimeMillis;

    public DefaultCurrentRoadTracker(List<Segment> routeToFollow, Double currentLatitude,
                                     Double currentLongitude) {
        this.routeToFollow = new ArrayDeque<>(routeToFollow);
        this.progressOnCurrentSegment =
                this.routeToFollow.getFirst().getCost() * getRelativeProgressOnRoadSegment(currentLatitude, currentLongitude, this.routeToFollow.getFirst());
        this.lastRecordedTimeMillis = System.currentTimeMillis();
    }


    // Относительная степень прохождения дороги. Число от 0 до 1
    private double getRelativeProgressOnRoadSegment(double latitude, double longitude, Segment segment) {
        double distanceToPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getStartNode().getLatitude(), segment.getStartNode().getLongitude());
        double distanceFromPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getEndNode().getLatitude(), segment.getEndNode().getLongitude());
        return distanceToPoint / (distanceToPoint + distanceFromPoint);
    }

    @Override public Segment getCurrentRoad() {
        return routeToFollow.getFirst();
    }

    private double skipAllPassedRoads(double time) {
        while (!routeToFollow.isEmpty() && progressOnCurrentSegment + time > routeToFollow.getFirst().getCost()) {
            log.info("road passed " + routeToFollow.getFirst());
            time -= routeToFollow.getFirst().getCost() - progressOnCurrentSegment;
            if (routeToFollow.size() > 1) {
                routeToFollow.pollFirst();
            }
            progressOnCurrentSegment = 0;
        }
        return time;
    }


    @Override public void updateCurrentRoad() {
        long now = System.currentTimeMillis();
        double time = (now - lastRecordedTimeMillis) / 1000d;
        time = skipAllPassedRoads(time);
        progressOnCurrentSegment += time;
        lastRecordedTimeMillis = now;
    }
}
