package com.nocmok.orp.road_index.mem_stub.service;

import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceOuterClass.GetShortestRouteRequest;
import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceOuterClass.GetShortestRouteResponse;
import com.nocmok.orp.road_index.mem_stub.solver.DijkstraSolver;
import io.grpc.stub.StreamObserver;

public class ShortestRouteSolverService extends ShortestRouteSolverServiceGrpc.ShortestRouteSolverServiceImplBase {

    private final DijkstraSolver solver;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public ShortestRouteSolverService(DijkstraSolver solver) {
        this.solver = solver;
    }

    @Override public void getShortestRoute(GetShortestRouteRequest request,
                                           StreamObserver<GetShortestRouteResponse> responseObserver) {
        var internalRoute = solver.getShortestRoute(request.getOriginNodeId(), request.getDestinationNodeId());
        responseObserver.onNext(GetShortestRouteResponse.newBuilder()
                .setRoute(graphApiMapper.mapInternalRouteToProtobufRoute(internalRoute))
                .build());
        responseObserver.onCompleted();
    }
}
