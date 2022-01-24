package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.ShortestPathSolver;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.SimpleORPInstance;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import com.nocmok.orp.proto.solver.taxi.TaxiSolver;
import com.nocmok.orp.proto.solver.vshs.VSHSSolver;
import com.nocmok.orp.proto.solver.vskt.ScheduleTree;
import com.nocmok.orp.proto.solver.vskt.VSKTORPInstance;
import com.nocmok.orp.proto.solver.vskt.VSKTSolver;
import com.nocmok.orp.proto.solver.vskt.VSKTVehicle;
import com.nocmok.orp.proto.solver.vsls.VSLSSolver;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Benchmarking {

    private Random random = new Random(1);
    private Graph graph;
    private ShortestPathSolver shortestPathSolver;
    private List<Integer> vehicleInitialNodes;
    private List<GPS> vehicleInitialGPS;
    private List<Benchmark.DelayedRequest> requestPlan;
    private int nIterations = 86_400;
    private int nRequests = 1000;
    private int nVehicles = 10;
    private double avgVehicleVelocity = 40;
    private int maxRidesharingLagSeconds = 480;
    private int maxClientWaitingTimeSeconds = 480;

    public Benchmarking() {
        this.graph = loadGraph();
        this.shortestPathSolver = new ShortestPathSolver(graph);

        this.requestPlan = new ArrayList<>();
        for (int i = 0; i < nRequests; ++i) {
            int time = random.nextInt(nIterations + 1);
            int startNode = random.nextInt(graph.nNodes());
            int endNode;
            while ((endNode = random.nextInt(graph.nNodes())) == startNode) {
            }
            requestPlan.add(new Benchmark.DelayedRequest(createRequest(time, startNode, endNode), time));
        }
        requestPlan.sort(Comparator.comparingInt(Benchmark.DelayedRequest::getTimeToAccept));

        this.vehicleInitialNodes = new ArrayList<>();
        this.vehicleInitialGPS = new ArrayList<>();
        for (int i = 0; i < nVehicles; ++i) {
            int node = random.nextInt(graph.nNodes());
            vehicleInitialNodes.add(node);
            vehicleInitialGPS.add(graph.getGps(node));
        }
    }

    private static void runBenchmark(String solver, Benchmark benchmark) {
        long start;
        long stop;
        System.out.println("orp solver=" + solver);
        start = System.currentTimeMillis();
        System.out.println(benchmark.runBenchmarking());
        stop = System.currentTimeMillis();
        System.out.println("elapsed=" + ((stop - start) / 1000) + " (s)");
    }

    public static void main(String[] args) {
        var benchMarking = new Benchmarking();
        runBenchmark(TaxiSolver.class.toString(), benchMarking.getTaxiBenchmark());
        System.out.println();
        runBenchmark(VSLSSolver.class.toString(), benchMarking.getVSLSBenchmark());
        System.out.println();
        runBenchmark(VSKTSolver.class.toString(), benchMarking.getVSKTBenchmark());
        System.out.println();
        runBenchmark(VSHSSolver.class.toString(), benchMarking.getVSHSBenchmark());
    }

    private Request createRequest(int time, int startNode, int endNode) {
        double shortestDistance = shortestPathSolver.dijkstra(startNode, endNode).getDistance();
        int shortestRouteTime = (int) (shortestDistance / avgVehicleVelocity);

        return Request.builder()
                .requestId(0)
                .userId(0)
                .departurePoint(graph.getGps(startNode))
                .arrivalPoint(graph.getGps(endNode))
                .departureNode(startNode)
                .arrivalNode(endNode)
                .departureTimeWindow(new int[]{time, time + maxClientWaitingTimeSeconds})
                .arrivalTimeWindow(new int[]{time, time + maxClientWaitingTimeSeconds + maxRidesharingLagSeconds + shortestRouteTime})
                .load(1)
                .state(Request.State.PENDING)
                .build();
    }

    private Graph loadGraph() {
        try {
            var dimacsParser = new DimacsParser();
            var gr = dimacsParser.readGr(Benchmark.class.getClassLoader().getResourceAsStream("ny131.gr"));
            var co = dimacsParser.readCo(Benchmark.class.getClassLoader().getResourceAsStream("ny131.co"));
            return new DimacsGraphConverter().convert(gr, co);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Benchmark getVSLSBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new VSLSSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    private Benchmark getVSHSBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new VSHSSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    private Benchmark getVSKTBenchmark() {
        var state = new VSKTORPInstance(graph);
        for (var gps : vehicleInitialGPS) {
            state.addVehicle(new VSKTVehicle(gps, Vehicle.State.PENDING, avgVehicleVelocity, (v) -> new ScheduleTree(v, state)));
        }
        var solver = new VSKTSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getTaxiBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new TaxiSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }
}
