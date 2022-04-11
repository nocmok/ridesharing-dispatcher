package com.nocmok.orp.road_index.mem_stub.service;

import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodeRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodeResponse;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodesRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetNodesResponse;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentResponse;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentsRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphMetadataStorageServiceOuterClass.GetSegmentsResponse;
import com.nocmok.orp.road_index.mem_stub.solver.GraphMetadataStorageImpl;
import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class GraphMetadataStorageService extends GraphMetadataStorageServiceGrpc.GraphMetadataStorageServiceImplBase {

    private final GraphMetadataStorageImpl graphMetadataStorage;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphMetadataStorageService(GraphMetadataStorageImpl graphMetadataStorage) {
        this.graphMetadataStorage = graphMetadataStorage;
    }

    @Override public void getSegment(GetSegmentRequest request,
                                     StreamObserver<GetSegmentResponse> responseObserver) {
        var internalSegment = graphMetadataStorage.getSegment(request.getStartNodeId(), request.getEndNodeId());
        responseObserver.onNext(GetSegmentResponse.newBuilder()
                .setSegment(graphApiMapper.mapInternalSegmentToServiceSegment(internalSegment))
                .build());
        responseObserver.onCompleted();
    }

    @Override public void getNode(GetNodeRequest request,
                                  StreamObserver<GetNodeResponse> responseObserver) {
        var internalNode = graphMetadataStorage.getNode(request.getNodeId());
        responseObserver.onNext(GetNodeResponse.newBuilder()
                .setNode(graphApiMapper.mapInternalNodeToProtobufNode(internalNode))
                .build());
        responseObserver.onCompleted();
    }

    @Override public void getSegments(GetSegmentsRequest request,
                                      StreamObserver<GetSegmentsResponse> responseObserver) {
        var internalSegments = graphMetadataStorage.getSegments(
                request.getStartNodeIdsList().subList(0, request.getStartNodeIdsCount()),
                request.getEndNodeIdsList().subList(0, request.getEndNodeIdsCount())
        );
        responseObserver.onNext(GetSegmentsResponse.newBuilder()
                .addAllSegments(internalSegments.stream()
                        .map(graphApiMapper::mapInternalSegmentToServiceSegment)
                        .collect(Collectors.toList()))
                .build());
        responseObserver.onCompleted();
    }

    @Override public void getNodes(GetNodesRequest request,
                                   StreamObserver<GetNodesResponse> responseObserver) {
        var internalNodes = graphMetadataStorage.getNodes(request.getIdsList().subList(0, request.getIdsCount()));
        responseObserver.onNext(GetNodesResponse.newBuilder()
                .addAllNodes(internalNodes.stream()
                        .map(graphApiMapper::mapInternalNodeToProtobufNode)
                        .collect(Collectors.toList()))
                .build());
        responseObserver.onCompleted();
    }
}
