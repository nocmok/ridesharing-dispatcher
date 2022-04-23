package com.nocmok.orp.simulator.service.telemetry;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.tools.EarthMath;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FollowScheduleWalk implements WalkStrategy {

    private String sessionId;
    private double accuracy = 1;

    private List<Segment> routeToFollow;
    private double latitude;
    private double longitude;

    private Segment currentSegment;
    private double progressOnCurrentSegment;
    private Iterator<Segment> nextSegment;

    public FollowScheduleWalk(String sessionId, List<Segment> routeToFollow, Double currentLatitude, Double currentLongitude) {
        if (routeToFollow.isEmpty()) {
            throw new IllegalArgumentException("route should not be empty");
        }
        if (currentLatitude == null) {
            currentLatitude = routeToFollow.get(0).getStartNode().getLatitude();
            currentLongitude = routeToFollow.get(0).getStartNode().getLongitude();
        }
        this.sessionId = sessionId;
        this.routeToFollow = new ArrayList<>(routeToFollow);
        this.nextSegment = this.routeToFollow.iterator();
        this.currentSegment = nextSegment.next();
        this.progressOnCurrentSegment = currentSegment.getCost() * getRelativeProgressOnRoadSegment(latitude, longitude, currentSegment);
        this.latitude = currentLatitude;
        this.longitude = currentLongitude;
    }

    public FollowScheduleWalk(String sessionId, List<Segment> routeToFollow) {
        this(sessionId, routeToFollow, null, null);
    }

    // Относительная степень прохождения дороги. Число от 0 до 1
    private double getRelativeProgressOnRoadSegment(double latitude, double longitude, Segment segment) {
        double distanceToPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getStartNode().getLatitude(), segment.getStartNode().getLongitude());
        double distanceFromPoint =
                EarthMath.spheroidalDistanceDegrees(latitude, longitude, segment.getEndNode().getLatitude(), segment.getEndNode().getLongitude());
        return distanceToPoint / (distanceToPoint + distanceFromPoint);
    }

    // Проматывает все ребра в маршруте которые были пройдены за указанное время.
    private double skipAllPassedRoads(double time) {
        while (progressOnCurrentSegment + time > currentSegment.getCost() && nextSegment.hasNext()) {
            time -= currentSegment.getCost() - progressOnCurrentSegment;
            progressOnCurrentSegment = 0;
            currentSegment = nextSegment.next();
        }
        return time;
    }

    @Override public Telemetry nextTelemetry(double time) {
        time = skipAllPassedRoads(time);

        progressOnCurrentSegment += time;

        if (progressOnCurrentSegment >= currentSegment.getCost()) {
            // Если закончили с последней дорогой в маршруте
            this.latitude = currentSegment.getEndNode().getLatitude();
            this.longitude = currentSegment.getEndNode().getLongitude();
        } else {
            this.latitude = currentSegment.getStartNode().getLatitude() +
                    progressOnCurrentSegment * (currentSegment.getEndNode().getLatitude() - currentSegment.getStartNode().getLatitude()) /
                            currentSegment.getCost();
            this.longitude = currentSegment.getStartNode().getLongitude() +
                    progressOnCurrentSegment * (currentSegment.getEndNode().getLongitude() - currentSegment.getStartNode().getLongitude()) /
                            currentSegment.getCost();
        }

        return Telemetry.builder()
                .sessionId(sessionId)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .accuracy(accuracy)
                .recordedAt(Instant.now())
                .build();
    }
}
