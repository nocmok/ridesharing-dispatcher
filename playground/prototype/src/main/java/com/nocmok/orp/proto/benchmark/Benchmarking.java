package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.ShortestPathSolver;
import com.nocmok.orp.proto.solver.TShareSolver;
import com.nocmok.orp.proto.solver.TaxiSolver;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Benchmarking {

    private Random random = new Random(1000);
    private Graph graph;
    private ShortestPathSolver shortestPathSolver;
    private List<Integer> vehicleInitialNodes;
    private List<Benchmark.DelayedRequest> requestPlan;
    private int nIterations = 86_400;
    private int nRequests = 10000;
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
            while((endNode = random.nextInt(graph.nNodes())) == startNode) {
            }
            requestPlan.add(new Benchmark.DelayedRequest(createRequest(time, startNode, endNode), time));
        }
        requestPlan.sort(Comparator.comparingInt(Benchmark.DelayedRequest::getTimeToAccept));

        this.vehicleInitialNodes = new ArrayList<>();
        for(int i = 0; i < nVehicles; ++i) {
            vehicleInitialNodes.add(random.nextInt(graph.nNodes()));
        }
    }

    public static void main(String[] args) {
        var benchMarking = new Benchmarking();
        long start;
        long stop;
        System.out.println("orp solver=" + TShareSolver.class);
        start = System.currentTimeMillis();
        System.out.println(benchMarking.getTShareBenchmark().runBenchmarking());
        stop = System.currentTimeMillis();
        System.out.println("elapsed=" + ((stop - start) / 1000) + " (s)");
        System.out.println();
        System.out.println("orp solver=" + TaxiSolver.class);
        start = System.currentTimeMillis();
        System.out.println(benchMarking.getTaxiBenchmark().runBenchmarking());
        stop = System.currentTimeMillis();
        System.out.println("elapsed=" + ((stop - start) / 1000) + " (s)");
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

    private ORPInstance getORPInstance() {
        return new ORPInstance(graph, vehicles());
    }

    private List<Vehicle> vehicles() {
        return vehicleInitialNodes.stream()
                .map(graph::getGps)
                .map(gps -> new Vehicle(gps, Vehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList());
    }

    public Benchmark getTShareBenchmark() {
        var state = getORPInstance();
        var solver = new TShareSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getTaxiBenchmark() {
        var state = getORPInstance();
        var solver = new TaxiSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .nIterations(nIterations)
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }
}
