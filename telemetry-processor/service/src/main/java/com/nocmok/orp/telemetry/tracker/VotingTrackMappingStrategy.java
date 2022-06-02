package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VotingTrackMappingStrategy implements VehicleTrackMappingStrategy {

    private SpatialGraphUtils spatialGraphUtils;

    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата привязки
     * к направлению движения тс относительно дороги к которой привязывается тс
     */
    private double directionSensity;
    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата
     * к расстоянию тс от ребра к которому привязывается тс
     */
    private double distanceSensity;
    /**
     * Сколько раз подряд ребру должна быть выдана максимальная оценка среди остальных ребер для того, чтобы ребро заматчилось
     */
    private int votesThreshold;
    /**
     * Коэффициент на который будут умножаться вероятности ребер из подсказки.
     * Так как вероятности обратные, то чем ближе число к нулю, тем вероятнее будет выбрано ребро из подсказки
     */
    private double hintContribution;

    public VotingTrackMappingStrategy(SpatialGraphUtils spatialGraphUtils, double directionSensity, double distanceSensity, int votesThreshold,
                                      double hintContribution) {
        this.spatialGraphUtils = spatialGraphUtils;
        this.directionSensity = directionSensity;
        this.distanceSensity = distanceSensity;
        this.votesThreshold = votesThreshold;
        this.hintContribution = hintContribution;
    }

    @Override public List<RoadSegment> matchTrackToGraph(List<LatLon> latLonTrack) {
        return matchTrackToGraph(latLonTrack, Collections.emptyList());
    }

    private double dotProduct(LatLon a, LatLon b) {
        return a.getLatitude() * b.getLatitude() + a.getLongitude() * b.getLongitude();
    }

    private LatLon sub(LatLon a, LatLon b) {
        return new LatLon(a.getLatitude() - b.getLatitude(), a.getLongitude() - b.getLongitude());
    }

    private LatLon sum(LatLon a, LatLon b) {
        return new LatLon(a.getLatitude() + b.getLatitude(), a.getLongitude() + b.getLongitude());
    }

    private LatLon mul(LatLon a, double scalar) {
        return new LatLon(a.getLatitude() * scalar, a.getLongitude() * scalar);
    }

    private double abs2(LatLon a) {
        return a.getLatitude() * a.getLatitude() + a.getLongitude() * a.getLongitude();
    }

    private double distance(LatLon a, LatLon b) {
        return Math.hypot(a.getLatitude() - b.getLatitude(), a.getLongitude() - b.getLongitude());
    }

    private double getDirectionRate(LatLon roadStartNode, LatLon roadEndNode, LatLon trackStartNode, LatLon trackEndNode) {
        double cos = dotProduct(sub(roadEndNode, roadStartNode), sub(trackEndNode, trackStartNode)) /
                distance(roadEndNode, roadStartNode) / distance(trackEndNode, trackStartNode);
        return Double.max(0, Double.min(1, (1 - cos) / 2));
    }

    private double getDistanceRate(LatLon roadStartNode, LatLon roadEndNode, LatLon trackStartNode, LatLon trackEndNode) {
        var E = sub(roadEndNode, roadStartNode);
        var P = sub(trackEndNode, roadStartNode);
        return distance(trackEndNode, sum(roadStartNode, mul(E, dotProduct(P, E) / abs2(E))));
    }

    private double getRoadRate(LatLon roadStartNode, LatLon roadEndNode, LatLon trackStartNode, LatLon trackEndNode) {
        double distance = getDistanceRate(roadStartNode, roadEndNode, trackStartNode, trackEndNode);
        double direction = getDirectionRate(roadStartNode, roadEndNode, trackStartNode, trackEndNode);
        return Math.pow(distance, distanceSensity) + Math.pow(direction, directionSensity) + distance + direction;
    }

    @Override public List<RoadSegment> matchTrackToGraph(List<LatLon> latLonTrack, List<RoadSegment> hint) {
        // Точность измерения gps
        final double accuracy = 10;

        // Если в треке недостаточно gps для того чтобы заматчить даже одно ребро, то пропускаем
        if (latLonTrack.size() < votesThreshold + 1) {
            return Collections.emptyList();
        }

        var hintSet = CollectionUtils.isEmpty(hint) ? Collections.emptySet() : new HashSet<>(hint);
        var matching = new ArrayList<Segment>();

        Segment lastVotedRoad = null;
        int votes = 0;

        for (int i = 1; i < latLonTrack.size(); ++i) {
            var trackStartNode = latLonTrack.get(i - 1);
            var trackEndNode = latLonTrack.get(i);

            var candidateRoads =
                    spatialGraphUtils.getRoadSegmentsWithinCircleArea(trackEndNode.getLatitude(), trackEndNode.getLongitude(), accuracy);

            if (candidateRoads.isEmpty()) {
                continue;
            }

            var bestRoad = candidateRoads.stream()
                    .min(Comparator.comparingDouble(
                            road -> (hintSet.contains(road) ? hintContribution : 1d) *
                                    getRoadRate(
                                            new LatLon(road.getStartNode().getLatitude(), road.getStartNode().getLongitude()),
                                            new LatLon(road.getEndNode().getLatitude(), road.getEndNode().getLongitude()),
                                            trackStartNode,
                                            trackEndNode)
                    ))
                    .get();

            if (!Objects.equals(lastVotedRoad, bestRoad)) {
                lastVotedRoad = bestRoad;
                votes = 1;
                continue;
            }

            ++votes;
            if (votes < votesThreshold) {
                continue;
            }

            if (!matching.isEmpty()) {
                if (Objects.equals(matching.get(matching.size() - 1), bestRoad)) {
                    continue;
                }
            }

            matching.add(bestRoad);
        }

        return matching.stream()
                .map(segment -> new RoadSegment(segment.getStartNode().getId(), segment.getEndNode().getId()))
                .collect(Collectors.toUnmodifiableList());
    }
}
