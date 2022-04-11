package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceGrpc.GraphMetadataStorageServiceBlockingStub;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodeRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodesRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentsRequest;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class GraphMetadataStorageImpl implements SpatialGraphMetadataStorage {

    private final GraphMetadataStorageServiceBlockingStub blockingStub;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphMetadataStorageImpl(String host, Integer port) {
        this.blockingStub = GraphMetadataStorageServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    @Override public Segment getSegment(String startNodeId, String endNodeId) {
        var segment = blockingStub.getSegment(GetSegmentRequest.newBuilder()
                .setStartNodeId(startNodeId)
                .setEndNodeId(endNodeId)
                .build()).getSegment();
        return graphApiMapper.mapProtobufSegmentToInternalSegment(segment);
    }

    @Override public Node getNode(String id) {
        var node = blockingStub.getNode(GetNodeRequest.newBuilder()
                .setNodeId(id)
                .build()).getNode();
        return graphApiMapper.mapProtobufNodeToInternalNode(node);
    }

    @Override public List<Segment> getSegments(List<String> startNodeIds, List<String> endNodeIds) {
        var segments = blockingStub.getSegments(GetSegmentsRequest.newBuilder()
                .addAllStartNodeIds(startNodeIds)
                .addAllEndNodeIds(endNodeIds)
                .build()).getSegmentsList();
        return segments.stream()
                .map(graphApiMapper::mapProtobufSegmentToInternalSegment)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public List<Node> getNodes(List<String> ids) {
        var nodes = blockingStub.getNodes(GetNodesRequest.newBuilder()
                .addAllIds(ids)
                .build()).getNodesList();
        return nodes.stream()
                .map(graphApiMapper::mapProtobufNodeToInternalNode)
                .collect(Collectors.toUnmodifiableList());
    }
}
