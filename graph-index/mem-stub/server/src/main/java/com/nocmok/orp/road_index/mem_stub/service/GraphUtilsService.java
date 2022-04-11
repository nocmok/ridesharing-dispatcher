package com.nocmok.orp.road_index.mem_stub.service;

import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphUtilsServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.GraphUtilsServiceOuterClass;
import com.nocmok.orp.road_index.mem_stub.solver.GraphUtilsImpl;
import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class GraphUtilsService extends GraphUtilsServiceGrpc.GraphUtilsServiceImplBase {

    private final GraphUtilsImpl graphUtils;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public GraphUtilsService(GraphUtilsImpl graphUtils) {
        this.graphUtils = graphUtils;
    }

    @Override public void getRoadSegmentsWithinCircleArea(GraphUtilsServiceOuterClass.GetRoadSegmentsWithinCircleAreaRequest request,
                                                          StreamObserver<GraphUtilsServiceOuterClass.GetRoadSegmentsWithinCircleAreaResponse> responseObserver) {
        var internalSegments = graphUtils.getRoadSegmentsWithinCircleArea(request.getCenterLatitude(), request.getCenterLongitude(), request.getRadius());
        responseObserver.onNext(GraphUtilsServiceOuterClass.GetRoadSegmentsWithinCircleAreaResponse.newBuilder()
                .addAllSegments(internalSegments.stream()
                        .map(graphApiMapper::mapInternalSegmentToServiceSegment)
                        .collect(Collectors.toUnmodifiableList()))
                .build());
        responseObserver.onCompleted();
    }
}
