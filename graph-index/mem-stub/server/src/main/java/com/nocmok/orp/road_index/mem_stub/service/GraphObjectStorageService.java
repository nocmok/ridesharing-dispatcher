package com.nocmok.orp.road_index.mem_stub.service;

import com.google.protobuf.NullValue;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphObjectStorageServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphObjectStorageServiceOuterClass;
import com.nocmok.orp.road_index.mem_stub.solver.GraphObjectStorageImpl;
import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class GraphObjectStorageService extends GraphObjectStorageServiceGrpc.GraphObjectStorageServiceImplBase {

    private final GraphObjectStorageImpl graphObjectStorage;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphObjectStorageService(GraphObjectStorageImpl graphObjectStorage) {
        this.graphObjectStorage = graphObjectStorage;
    }

    @Override public void getObject(GraphObjectStorageServiceOuterClass.GetObjectRequest request,
                                    StreamObserver<GraphObjectStorageServiceOuterClass.GetObjectResponse> responseObserver) {
        var graphObject = graphObjectStorage.getObject(request.getId());
        var getObjectResponseBuilder = GraphObjectStorageServiceOuterClass.GetObjectResponse.newBuilder();
        if (graphObject.isPresent()) {
            getObjectResponseBuilder.setData(graphApiMapper.mapInternalGraphObjectToProtobufGraphObject(graphObject.get()));
        } else {
            getObjectResponseBuilder.setNull(NullValue.NULL_VALUE);
        }
        responseObserver.onNext(getObjectResponseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override public void getNeighborhood(GraphObjectStorageServiceOuterClass.GetNeighborhoodRequest request,
                                          StreamObserver<GraphObjectStorageServiceOuterClass.GetNeighborhoodResponse> responseObserver) {
        var objects = graphObjectStorage.getNeighborhood(request.getNodeId(), request.getRadius());
        responseObserver.onNext(GraphObjectStorageServiceOuterClass.GetNeighborhoodResponse.newBuilder()
                .addAllNeighborhood(objects.stream()
                        .map(graphApiMapper::mapInternalGraphObjectToProtobufGraphObject)
                        .collect(Collectors.toUnmodifiableList()))
                .build());
        responseObserver.onCompleted();
    }

    @Override public void updateObject(GraphObjectStorageServiceOuterClass.UpdateObjectRequest request,
                                       StreamObserver<GraphObjectStorageServiceOuterClass.UpdateObjectResponse> responseObserver) {
        graphObjectStorage.updateObject(graphApiMapper.mapProtobufObjectUpdaterToInternalObjectUpdater(request.getObjectUpdater()));
        responseObserver.onNext(GraphObjectStorageServiceOuterClass.UpdateObjectResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override public void removeObject(GraphObjectStorageServiceOuterClass.RemoveObjectRequest request,
                                       StreamObserver<GraphObjectStorageServiceOuterClass.RemoveObjectResponse> responseObserver) {
        graphObjectStorage.removeObject(request.getObjectId());
        responseObserver.onNext(GraphObjectStorageServiceOuterClass.RemoveObjectResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
