package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.graph.tools.EarthMath;

import java.util.List;
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
                .flatMap(node -> graph.getOutboundLinksMap(node.getId()).values().stream())
                .filter(road -> checkRoadSegmentInsideCircleArea(centerLatitude, centerLongitude, radius, road))
                .map(this::mapInternalLinkToGraphApiSegment)
                .collect(Collectors.toUnmodifiableList());

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
