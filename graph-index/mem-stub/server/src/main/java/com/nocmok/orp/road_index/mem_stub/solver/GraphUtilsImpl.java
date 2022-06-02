package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.graph.tools.EarthMath;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GraphUtilsImpl implements SpatialGraphUtils {

    private final Graph graph;

    public GraphUtilsImpl(Graph graph) {
        this.graph = graph;
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

    private LatLon getPointOnLineProjection(LatLon point, LatLon lineStart, LatLon lineEnd) {
        LatLon B = sub(lineEnd, lineStart);
        LatLon C = sub(point, lineStart);
        return sum(lineStart, mul(B, dotProduct(B, C) / abs2(B)));
    }

    private Node mapInternalNodeToGraphApiNode(Graph.Node nodeMetadata) {
        return new com.nocmok.orp.graph.api.Node(
                nodeMetadata.getId(),
                nodeMetadata.getLatitude(),
                nodeMetadata.getLongitude()
        );
    }

    private Segment mapInternalLinkToGraphApiSegment(Graph.Link link) {
        return new Segment(link.getId(),
                mapInternalNodeToGraphApiNode(graph.getNodeMetadata(link.getStartNodeId())),
                mapInternalNodeToGraphApiNode(graph.getNodeMetadata(link.getEndNodeId())),
                link.getCost());
    }

    private boolean checkRoadSegmentInsideCircleArea(double centerLatitude, double centerLongitude, double radius, Graph.Link road) {
        var center = new LatLon(centerLatitude, centerLongitude);
        var roadA =
                new LatLon(graph.getNodeMetadata(road.getStartNodeId()).getLatitude(), graph.getNodeMetadata(road.getStartNodeId()).getLongitude());
        var roadB = new LatLon(graph.getNodeMetadata(road.getEndNodeId()).getLatitude(), graph.getNodeMetadata(road.getEndNodeId()).getLongitude());
        var projection = getPointOnLineProjection(center, roadA, roadB);

        if (EarthMath.spheroidalDistanceDegrees(centerLatitude, centerLongitude, projection.getLatitude(), projection.getLongitude()) > radius) {
            return false;
        }

        if (projection.getLatitude() >= roadA.getLatitude() && projection.getLatitude() <= roadB.getLatitude()) {
            return true;
        }

        if (projection.getLatitude() >= roadB.getLatitude() && projection.getLatitude() <= roadA.getLatitude()) {
            return true;
        }

        if (EarthMath.spheroidalDistanceDegrees(centerLatitude, centerLongitude, roadA.getLatitude(), roadA.getLongitude()) <= radius) {
            return true;
        }

        return EarthMath.spheroidalDistanceDegrees(centerLatitude, centerLongitude, roadB.getLatitude(), roadB.getLongitude()) <= radius;
    }

    @Override public List<Segment> getRoadSegmentsWithinCircleArea(double centerLatitude, double centerLongitude, double radius) {
        return graph.getAllNodes().stream()
                .flatMap(node -> graph.getOutboundLinksMap(node.getId()).values()
                        .stream())
                .filter(road -> checkRoadSegmentInsideCircleArea(centerLatitude, centerLongitude, radius, road))
                .map(this::mapInternalLinkToGraphApiSegment)
                .collect(Collectors.toUnmodifiableList());

    }

    private boolean pointInsideSegment(double latitude, double longitude, Segment segment) {
        return latitude >= segment.getStartNode().getLatitude() && latitude <= segment.getEndNode().getLatitude()
                || latitude >= segment.getEndNode().getLatitude() && latitude <= segment.getStartNode().getLatitude();
    }

    private double getDistanceToRoadSegment(double latitude, double longitude, Segment segment) {
        var source = new LatLon(segment.getStartNode().getLatitude(), segment.getStartNode().getLongitude());
        var target = new LatLon(segment.getEndNode().getLatitude(), segment.getEndNode().getLongitude());
        var projection = getPointOnLineProjection(new LatLon(latitude, longitude), source, target);

        if (pointInsideSegment(projection.getLatitude(), projection.getLongitude(), segment)) {
            return EarthMath.spheroidalDistanceDegrees(latitude, longitude, projection.getLatitude(), projection.getLongitude());
        } else {
            double sourceDistance = EarthMath.spheroidalDistanceDegrees(latitude, longitude, source.getLatitude(), source.getLongitude());
            double targetDistance = EarthMath.spheroidalDistanceDegrees(latitude, longitude, target.getLatitude(), target.getLongitude());
            return Double.min(sourceDistance, targetDistance);
        }
    }

    private boolean isLatLonInRightSemiPlane(double latitude, double longitude, Segment segment) {
        // Берем вектор от source до target = a.
        // Берем вектор от source до latLon = b.
        // Вычисляем векторное произведение векторов a x b

        // затем берем координату z полученного вектора
        // если Z > 0 то наша точка в левой полуплоскости, иначе в правой

        double ax = segment.getEndNode().getLongitude() - segment.getStartNode().getLongitude();
        double ay = segment.getEndNode().getLatitude() - segment.getStartNode().getLatitude();

        double bx = longitude - segment.getStartNode().getLongitude();
        double by = latitude - segment.getEndNode().getLatitude();

        return (ax * by - ay * bx) <= 0;
    }

    private boolean linkHasReversedLink(Graph.Link link) {
        return Objects.requireNonNullElse(graph.getOutboundLinksMap(link.getEndNodeId()), Collections.emptyMap())
                .containsKey(link.getStartNodeId());
    }

    @Override public Segment getClosestRoadSegment(double latitude, double longitude, boolean rightHandTraffic) {
        // Если есть две дороги с одинаковым расстоянием, то
        // Выбирается дорога относительно которой указанная точка находится в правильной полуплоскости
        // с точки зрения направления движения
        // Если все равно есть несколько подходящий дорог, то выбирается любая

        return graph.getAllNodes().stream()
                .flatMap(node -> graph.getOutboundLinksMap(node.getId()).values()
                        .stream())
                .filter(link -> {
                    if (!linkHasReversedLink(link)) {
                        return true;
                    }
                    if (rightHandTraffic) {
                        return isLatLonInRightSemiPlane(latitude, longitude, mapInternalLinkToGraphApiSegment(link));
                    } else {
                        return !isLatLonInRightSemiPlane(latitude, longitude, mapInternalLinkToGraphApiSegment(link));
                    }
                })
                .map(this::mapInternalLinkToGraphApiSegment)
                .min(Comparator.comparingDouble(segment -> getDistanceToRoadSegment(latitude, longitude, segment)))
                .orElse(null);
    }

    private static class LatLon {
        private final double latitude;
        private final double longitude;

        public LatLon(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override public String toString() {
            return "LatLon{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
