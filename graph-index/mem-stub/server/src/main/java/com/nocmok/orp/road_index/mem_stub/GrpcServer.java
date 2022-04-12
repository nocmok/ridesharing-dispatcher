package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.road_index.mem_stub.io.DimacsReader;
import com.nocmok.orp.road_index.mem_stub.io.GraphMLReader;
import com.nocmok.orp.road_index.mem_stub.io.GraphReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GrpcServer {

    private static final List<GraphReader> graphReaders = List.of(
            new DimacsReader(),
            new GraphMLReader()
    );

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private static Graph loadGraph() {
        File graphDirectory = new File(Objects.requireNonNullElse(System.getenv("GRAPH_DIRECTORY"), "/opt/graph"));

        if (!(graphDirectory.exists() && graphDirectory.isDirectory())) {
            throw new RuntimeException("directory with graph files doesn't exists " + graphDirectory.getAbsolutePath());
        }

        File[] graphFiles = graphDirectory.listFiles();

        if (graphFiles == null || graphFiles.length == 0) {
            throw new RuntimeException("directory with graph files is empty " + graphDirectory.getAbsolutePath());
        }

        var graphReader = graphReaders.stream()
                .filter(reader -> reader.canReadFiles(graphFiles))
                .findFirst();

        if (graphReader.isEmpty()) {
            throw new RuntimeException("cannot find reader to read files in directory " + graphDirectory);
        }

        log.info("use graph reader " + graphReader.get().getClass().getSimpleName() + " to read files "
                + Arrays.stream(graphFiles).map(File::getName).collect(Collectors.toUnmodifiableList()));

        return graphReader.get().readGraph(graphFiles);
    }

    public static void main(String[] args) throws Exception {
        var graph = loadGraph();

        log.info("successfully loaded graph");

        int port = Integer.parseInt(Objects.requireNonNullElse(System.getenv("GRPC_PORT"), "8080"));

        var server = ServerBuilder
                .forPort(port)
                .addService(new ShortestRouteSolverService(new DijkstraSolver(graph)))
                .addService(new GraphMetadataStorageService(new GraphMetadataStorageImpl(graph)))
                .addService(new GraphObjectStorageService(new GraphObjectStorageImpl(graph)))
                .addService(new GraphUtilsService(new GraphUtilsImpl(graph)))
                .build();

        log.info("grpc server will started on port " + port);
        log.info("pid " + ProcessHandle.current().pid());

        server.start();
        server.awaitTermination();
    }
}
