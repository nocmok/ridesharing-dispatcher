package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceGrpc;
import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceGrpc.ShortestRouteSolverServiceBlockingStub;
import com.nocmok.orp.road_index.mem_stub.server.grpc.ShortestRouteSolverServiceOuterClass.GetShortestRouteRequest;
import io.grpc.ManagedChannelBuilder;

public class ShortestRouteSolverImpl implements ShortestRouteSolver {

    private final ShortestRouteSolverServiceBlockingStub blockingStub;
    private final GraphApiMapper graphApiMapper = new GraphApiMapper();

    public ShortestRouteSolverImpl(String host, Integer port) {
        this.blockingStub = ShortestRouteSolverServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    @Override public Route getShortestRoute(String originNodeId, String destinationNodeId) {
        var route = blockingStub.getShortestRoute(GetShortestRouteRequest.newBuilder()
                .setOriginNodeId(originNodeId)
                .setDestinationNodeId(destinationNodeId)
                .build()).getRoute();

        return graphApiMapper.mapProtobufRouteToInternalRoute(route);
    }
}
