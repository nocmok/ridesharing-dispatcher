package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphApi;

import java.util.stream.Collectors;

class GraphApiMapper {

    public Segment mapProtobufSegmentToInternalSegment(GraphApi.Segment segment) {
        return new Segment(
                segment.getId(),
                mapProtobufNodeToInternalNode(segment.getStartNode()),
                mapProtobufNodeToInternalNode(segment.getEndNode()),
                segment.getCost()
        );
    }

    public Node mapProtobufNodeToInternalNode(GraphApi.Node node) {
        return new Node(
                node.getId(),
                node.getLatitude(),
                node.getLongitude()
        );
    }

    public Route mapProtobufRouteToInternalRoute(GraphApi.Route route) {
        return new Route(
                route.getSegmentsList().stream()
                        .map(this::mapProtobufSegmentToInternalSegment)
                        .collect(Collectors.toUnmodifiableList()),
                route.getCost()
        );
    }

    public SpatialGraphObject mapProtobufGraphObjectToInternalGraphObject(GraphApi.GraphObject graphObject) {
        return new SpatialGraphObject(
                graphObject.getId(),
                this.mapProtobufSegmentToInternalSegment(graphObject.getSegment()),
                graphObject.getLatitude(),
                graphObject.getLongitude()
        );
    }

    public GraphApi.ObjectUpdater mapInternalObjectUpdaterToProtobufObjectUpdater(ObjectUpdater objectUpdater) {
        return GraphApi.ObjectUpdater.newBuilder()
                .setId(objectUpdater.getId())
                .setSegmentStartNodeId(objectUpdater.getSegmentStartNodeId())
                .setSegmentEndNodeId(objectUpdater.getSegmentEndNodeId())
                .setLatitude(objectUpdater.getLatitude())
                .setLongitude(objectUpdater.getLongitude())
                .build();
    }
}
