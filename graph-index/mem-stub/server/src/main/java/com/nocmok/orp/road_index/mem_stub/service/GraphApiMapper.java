package com.nocmok.orp.road_index.mem_stub.service;

import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphApi;

import java.util.stream.Collectors;

public class GraphApiMapper {

    public GraphApi.Node mapInternalNodeToProtobufNode(Node node) {
        return GraphApi.Node.newBuilder()
                .setId(node.getId())
                .setLatitude(node.getLatitude())
                .setLongitude(node.getLongitude())
                .build();
    }

    public GraphApi.Segment mapInternalSegmentToServiceSegment(Segment segment) {
        return GraphApi.Segment.newBuilder()
                .setId(segment.getId())
                .setStartNode(mapInternalNodeToProtobufNode(segment.getStartNode()))
                .setEndNode(mapInternalNodeToProtobufNode(segment.getEndNode()))
                .setCost(segment.getCost())
                .build();
    }

    public GraphApi.Route mapInternalRouteToProtobufRoute(Route route) {
        return GraphApi.Route.newBuilder()
                .addAllSegments(route.getRoute().stream()
                        .map(this::mapInternalSegmentToServiceSegment)
                        .collect(Collectors.toList()))
                .setCost(route.getRouteCost())
                .build();
    }

    public GraphApi.GraphObject mapInternalGraphObjectToProtobufGraphObject(SpatialGraphObject graphObject) {
        if (graphObject == null) {
            return null;
        }
        return GraphApi.GraphObject.newBuilder()
                .setId(graphObject.getId())
                .setSegment(mapInternalSegmentToServiceSegment(graphObject.getSegment()))
                .setLatitude(graphObject.getLatitude())
                .setLongitude(graphObject.getLongitude())
                .build();
    }

    public ObjectUpdater mapProtobufObjectUpdaterToInternalObjectUpdater(GraphApi.ObjectUpdater objectUpdater) {
        return new ObjectUpdater(objectUpdater.getId(),
                objectUpdater.getSegmentStartNodeId(),
                objectUpdater.getSegmentEndNodeId(),
                objectUpdater.getLatitude(),
                objectUpdater.getLongitude());
    }
}
