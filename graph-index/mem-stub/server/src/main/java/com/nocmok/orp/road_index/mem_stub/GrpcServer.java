package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.road_index.mem_stub.io.DimacsReader;
import com.nocmok.orp.road_index.mem_stub.service.GraphMetadataStorageService;
import com.nocmok.orp.road_index.mem_stub.service.GraphObjectStorageService;
import com.nocmok.orp.road_index.mem_stub.service.GraphUtilsService;
import com.nocmok.orp.road_index.mem_stub.service.ShortestRouteSolverService;
import com.nocmok.orp.road_index.mem_stub.solver.DijkstraSolver;
import com.nocmok.orp.road_index.mem_stub.solver.Graph;
import com.nocmok.orp.road_index.mem_stub.solver.GraphMetadataStorageImpl;
import com.nocmok.orp.road_index.mem_stub.solver.GraphObjectStorageImpl;
import com.nocmok.orp.road_index.mem_stub.solver.GraphUtilsImpl;
import io.grpc.ServerBuilder;

import java.io.File;

public class GrpcServer {

    private static Graph loadGraph() {
        return new DimacsReader().readGraph(
                new File(ClassLoader.getSystemClassLoader().getResource("ny131.co").getFile()),
                new File(ClassLoader.getSystemClassLoader().getResource("ny131-t.gr").getFile()));
    }

    public static void main(String[] args) throws Exception {
        var graph = loadGraph();

        var server = ServerBuilder
                .forPort(8083)
                .addService(new ShortestRouteSolverService(new DijkstraSolver(graph)))
                .addService(new GraphMetadataStorageService(new GraphMetadataStorageImpl(graph)))
                .addService(new GraphObjectStorageService(new GraphObjectStorageImpl(graph)))
                .addService(new GraphUtilsService(new GraphUtilsImpl(graph)))
                .build();

        server.start();
        server.awaitTermination();
    }
}
