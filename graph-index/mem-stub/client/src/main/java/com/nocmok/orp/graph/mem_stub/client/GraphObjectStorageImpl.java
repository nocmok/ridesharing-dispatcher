package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphObjectStorageServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphObjectStorageServiceGrpc.GraphObjectStorageServiceBlockingStub;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphObjectStorageServiceOuterClass;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GraphObjectStorageImpl implements SpatialGraphObjectsStorage {

    private final GraphObjectStorageServiceBlockingStub blockingStub;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphObjectStorageImpl(String host, Integer port) {
        this.blockingStub = GraphObjectStorageServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    @Override public Optional<SpatialGraphObject> getObject(String id) {
        var graphObjectProtobuf = blockingStub.getObject(
                GraphObjectStorageServiceOuterClass.GetObjectRequest.newBuilder().setId(id).build());
        if (graphObjectProtobuf.hasNull()) {
            return Optional.empty();
        }
        return Optional.of(graphApiMapper.mapProtobufGraphObjectToInternalGraphObject(graphObjectProtobuf.getData()));
    }

    @Override public void updateObject(ObjectUpdater objectUpdater) {
        blockingStub.updateObject(
                GraphObjectStorageServiceOuterClass.UpdateObjectRequest.newBuilder()
                        .setObjectUpdater(graphApiMapper.mapInternalObjectUpdaterToProtobufObjectUpdater(objectUpdater))
                        .build());
    }

    @Override public void removeObject(String id) {
        blockingStub.removeObject(
                GraphObjectStorageServiceOuterClass.RemoveObjectRequest.newBuilder()
                        .setObjectId(id)
                        .build());
    }

    @Override public List<SpatialGraphObject> getNeighborhood(String nodeId, double radius) {
        return blockingStub.getNeighborhood(
                        GraphObjectStorageServiceOuterClass.GetNeighborhoodRequest.newBuilder()
                                .setNodeId(nodeId)
                                .setRadius(radius)
                                .build()
                ).getNeighborhoodList().stream()
                .map(graphApiMapper::mapProtobufGraphObjectToInternalGraphObject)
                .collect(Collectors.toUnmodifiableList());
    }
}
