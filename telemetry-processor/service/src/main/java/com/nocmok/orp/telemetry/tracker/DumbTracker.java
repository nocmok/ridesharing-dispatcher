package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphRoad;
import com.nocmok.orp.telemetry.api.GraphToolbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class DumbTracker implements VehicleTracker {

    private final GraphToolbox graphToolbox;
    private final DumbGPSMath math = new DumbGPSMath();

    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата привязки
     * к направлению движения тс относительно дороги к которой привязывается тс
     */
    private final double directonSensity = 1;

    /**
     * Гиперпараметр алгоритма привязки трека, определяющий чувствительность результата
     * к расстоянию тс от ребра к которому привязывается тс
     */
    private final double distanceSensity = 1;

    @Autowired
    public DumbTracker(GraphToolbox graphToolbox) {
        this.graphToolbox = graphToolbox;
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
     * Тупой алгоритм привязки трека к графу.
     * 1) Алгоритм считает, что все дороги имеют одинаковую геометрию и являются прямыми
     * 2) Алгоритм считает что точки расположены в евклидовом пространстве (например не учитывает естественное искривление земли)
     * 3) Алгоритм не учитывает погрешности измерений gps
     * <p>
     * Алгоритму нужны по крайней мере две точки, чтобы посчитать привязку.
     * Если передано меньше двух точек, то метод возвращает пустой список.
     * Если по переданному треку нельзя определить привязку (например все точки в треке равны),
     * то возвращается пустой список
     * <p>
     * Метод может вернуть набор несмежных ребер.
     * Чтобы посчитанная привязка состояла из цепочки смежных ребер нужно чтобы на каждом ребре
     * фактического маршрута было хотя бы одно измерение из трека
     */
    @Override public List<GraphRoad> matchTrackToGraph(List<GCS> track) {
        // Точность измерения gps
        final double accuracy = 10;
        // Минимальное расстояние между точками, чтобы считать их различными
        final double epsilon = accuracy;

        var cleanedTrack = new ArrayList<GCS>();
        for (var gcs : track) {
            if (cleanedTrack.isEmpty() || math.distance(cleanedTrack.get(cleanedTrack.size() - 1), gcs) > epsilon) {
                cleanedTrack.add(gcs);
            }
        }

        if (cleanedTrack.size() < 2) {
            return Collections.emptyList();
        }

        var matching = new ArrayList<GraphRoad>();

        for (int i = 1; i < cleanedTrack.size(); ++i) {
            var candidateRoads = graphToolbox.getRoadNeighborhood(cleanedTrack.get(i), accuracy);
            if (candidateRoads.isEmpty()) {
                continue;
            }
            var trackStartNode = track.get(i - 1);
            var trackEndNode = track.get(i);
            var bestRoad = candidateRoads.stream()
                    .min(Comparator.comparingDouble(
                            road -> getRoadRate(road.getStartNode().getCoordinates(), road.getEndNode().getCoordinates(), trackStartNode, trackEndNode)))
                    .get();

            if (!matching.isEmpty()) {
                if (Objects.equals(matching.get(matching.size() - 1), bestRoad)) {
                    continue;
                }
                // Простая коррекция ошибки
                // TODO добавить лукап в следующие n точек трека для оценивания вероятности ребра
                if (Objects.equals(matching.get(matching.size() - 1).getStartNode(), bestRoad.getStartNode())) {
                    matching.remove(matching.size() - 1);
                }
            }
            matching.add(bestRoad);
        }

        return matching;
    }

    @Override public GraphBinding getBinding(GraphRoad roadToBind, GCS gcs) {
        GCS S = roadToBind.getStartNode().getCoordinates();
        GCS E = math.sub(roadToBind.getEndNode().getCoordinates(), S);
        GCS P = math.sub(gcs, S);
        return new GraphBinding(roadToBind, math.dotProduct(P, E) / math.abs2(E));
    }
}
