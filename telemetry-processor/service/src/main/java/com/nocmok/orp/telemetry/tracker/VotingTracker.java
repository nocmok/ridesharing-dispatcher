package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphRoad;
import com.nocmok.orp.telemetry.api.GraphToolbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
public class VotingTracker implements VehicleTracker {

    private final GraphToolbox graphToolbox;
    private final DumbGPSMath math = new DumbGPSMath();
    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата привязки
     * к направлению движения тс относительно дороги к которой привязывается тс
     */
    @Value("${com.nocmok.orp.telemetry.tracker.VotingTracker.directonSensity:1}")
    private double directonSensity;
    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата
     * к расстоянию тс от ребра к которому привязывается тс
     */
    @Value("${com.nocmok.orp.telemetry.tracker.VotingTracker.distanceSensity:1}")
    private double distanceSensity;
    /**
     * Сколько раз подряд ребру должна быть выдана максимальная оценка среди остальных ребер для того, чтобы ребро заматчилось
     */
    @Value("${com.nocmok.orp.telemetry.tracker.VotingTracker.votesThreshold:3}")
    private int votesThreshold;
    /**
     * Коэффициент на который будут умножаться вероятности ребер из подсказки.
     * Так как вероятности обратные, то чем ближе число к нулю, тем вероятнее будет выбрано ребро из подсказки
     */
    @Value("${com.nocmok.orp.telemetry.tracker.VotingTracker.hintContribution:0.7}")
    private double hintContribution;

    @Autowired
    public VotingTracker(GraphToolbox graphToolbox) {
        this.graphToolbox = graphToolbox;
    }

    @Override public List<GraphRoad> matchTrackToGraph(List<GCS> track) {
        return matchTrackToGraph(track, Collections.emptyList());
    }

    private double getDirectionRate(GCS roadStartNode, GCS roadEndNode, GCS trackStartNode, GCS trackEndNode) {
        double cos = math.dotProduct(math.sub(roadEndNode, roadStartNode), math.sub(trackEndNode, trackStartNode)) /
                math.distance(roadEndNode, roadStartNode) / math.distance(trackEndNode, trackStartNode);
        return Double.max(0, Double.min(1, (1 - cos) / 2));
    }

    private double getDistanceRate(GCS roadStartNode, GCS roadEndNode, GCS trackStartNode, GCS trackEndNode) {
        GCS E = math.sub(roadEndNode, roadStartNode);
        GCS P = math.sub(trackEndNode, roadStartNode);
        return math.distance(trackEndNode, math.sum(roadStartNode, math.mul(E, math.dotProduct(P, E) / math.abs2(E))));
    }

    private double getRoadRate(GCS roadStartNode, GCS roadEndNode, GCS trackStartNode, GCS trackEndNode) {
        double distance = getDistanceRate(roadStartNode, roadEndNode, trackStartNode, trackEndNode);
        double direction = getDirectionRate(roadStartNode, roadEndNode, trackStartNode, trackEndNode);
        return Math.pow(distance, distanceSensity) + Math.pow(direction, directonSensity) + distance + direction;
    }

    /**
     * Описание алгоритма:
     * Трек разбивается на смежные пары.
     * Для каждой пары считается наилучшее ребро по направлению и дистанции.
     * Когда одно и то же ребро было выбрано наилучшим votesThreshold раз подряд, то оно добавляется в матчинг
     */
    @Override public List<GraphRoad> matchTrackToGraph(List<GCS> track, List<GraphRoad> hint) {
        // Точность измерения gps
        final double accuracy = 10;

        // Если в треке недостаточно gps для того чтобы заматчить даже одно ребро, то пропускаем
        if (track.size() < votesThreshold + 1) {
            return Collections.emptyList();
        }

        var hintSet = CollectionUtils.isEmpty(hint) ? Collections.<GraphRoad>emptySet() : new HashSet<>(hint);
        var matching = new ArrayList<GraphRoad>();

        GraphRoad lastVotedRoad = null;
        int votes = 0;

        for (int i = 1; i < track.size(); ++i) {
            var candidateRoads = graphToolbox.getRoadNeighborhood(track.get(i), accuracy);
            if (candidateRoads.isEmpty()) {
                continue;
            }
            var trackStartNode = track.get(i - 1);
            var trackEndNode = track.get(i);
            var bestRoad = candidateRoads.stream()
                    .min(Comparator.comparingDouble(
                            road -> (hintSet.contains(road) ? hintContribution : 1d) *
                                    getRoadRate(road.getStartNode().getCoordinates(), road.getEndNode().getCoordinates(), trackStartNode, trackEndNode)
                    ))
                    .get();

            if (!Objects.equals(lastVotedRoad, bestRoad)) {
                lastVotedRoad = bestRoad;
                votes = 0;
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

        return matching;
    }

    private double clamp(double value, double min, double max) {
        return Double.max(min, Double.min(max, value));
    }

    @Override public GraphBinding getBinding(GraphRoad roadToBind, GCS gcs) {
        GCS S = roadToBind.getStartNode().getCoordinates();
        GCS E = math.sub(roadToBind.getEndNode().getCoordinates(), S);
        GCS P = math.sub(gcs, S);
        return new GraphBinding(roadToBind, clamp(math.dotProduct(P, E) / math.abs2(E), 0, 1));
    }
}

