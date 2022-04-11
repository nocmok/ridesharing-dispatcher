package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphUtilsServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphUtilsServiceOuterClass;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class GraphUtilsImpl implements SpatialGraphUtils {

    private final GraphUtilsServiceGrpc.GraphUtilsServiceBlockingStub blockingStub;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphUtilsImpl(String host, Integer port) {
        this.blockingStub = GraphUtilsServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    @Override public List<Segment> getRoadSegmentsWithinCircleArea(double centerLatitude, double centerLongitude, double radius) {
        return blockingStub.getRoadSegmentsWithinCircleArea(GraphUtilsServiceOuterClass.GetRoadSegmentsWithinCircleAreaRequest.newBuilder()
                        .setCenterLatitude(centerLatitude)
                        .setCenterLongitude(centerLongitude)
                        .setRadius(radius)
                        .build()).getSegmentsList().stream()
                .map(graphApiMapper::mapProtobufSegmentToInternalSegment)
                .collect(Collectors.toUnmodifiableList());
    }
}
