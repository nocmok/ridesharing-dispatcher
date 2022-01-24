package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.ShortestPathSolver;
import com.nocmok.orp.proto.solver.common.SimpleORPInstance;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import com.nocmok.orp.proto.solver.taxi.TaxiSolver;
import com.nocmok.orp.proto.solver.vshs.VSHSSolver;
import com.nocmok.orp.proto.solver.vskt.ScheduleTree;
import com.nocmok.orp.proto.solver.vskt.VSHSKTSolver;
import com.nocmok.orp.proto.solver.vskt.VSKTORPInstance;
import com.nocmok.orp.proto.solver.vskt.VSKTSolver;
import com.nocmok.orp.proto.solver.vskt.VSKTVehicle;
import com.nocmok.orp.proto.solver.vsls.VSLSSolver;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Builder
public class Benchmarking {

    private ShortestPathSolver shortestPathSolver;
    private List<Integer> vehicleInitialNodes;
    private List<GPS> vehicleInitialGPS;
    private List<Benchmark.DelayedRequest> requestPlan;
    private Graph graph;

    @Builder.Default
    private Random random = new Random();
    @Builder.Default
    private int nIterations = 86_400;
    @Builder.Default
    private int nRequests = 1000;
    @Builder.Default
    private int nVehicles = 200;
    @Builder.Default
    private double avgVehicleVelocity = 40;
    @Builder.Default
    private int maxRidesharingLagSeconds = 480;
    @Builder.Default
    private int maxClientWaitingTimeSeconds = 480;
    @Builder.Default
    private int vehicleCapacity = 3;

    public Benchmarking(ShortestPathSolver shortestPathSolver, List<Integer> vehicleInitialNodes, List<GPS> vehicleInitialGPS,
                        List<Benchmark.DelayedRequest> requestPlan, Graph graph, Random random, int nIterations, int nRequests, int nVehicles,
                        double avgVehicleVelocity, int maxRidesharingLagSeconds, int maxClientWaitingTimeSeconds, int vehicleCapacity) {
        this.graph = graph;
        this.random = random;
        this.nIterations = nIterations;
        this.nRequests = nRequests;
        this.nVehicles = nVehicles;
        this.avgVehicleVelocity = avgVehicleVelocity;
        this.maxRidesharingLagSeconds = maxRidesharingLagSeconds;
        this.maxClientWaitingTimeSeconds = maxClientWaitingTimeSeconds;
        this.vehicleCapacity = vehicleCapacity;

        this.shortestPathSolver = new ShortestPathSolver(graph);
        this.requestPlan = getRequestPlan(nRequests);
        this.vehicleInitialNodes = getNodes(nVehicles);
        this.vehicleInitialGPS = getGPSList(this.vehicleInitialNodes);
    }

    private List<Benchmark.DelayedRequest> getRequestPlan(int nRequests) {
        var requestPlan = new ArrayList<Benchmark.DelayedRequest>();
        for (int i = 0; i < nRequests; ++i) {
            int time = random.nextInt(nIterations + 1);
            int startNode = random.nextInt(graph.nNodes());
            int endNode;
            while ((endNode = random.nextInt(graph.nNodes())) == startNode) {
            }
            requestPlan.add(new Benchmark.DelayedRequest(createRequest(time, startNode, endNode), time));
        }
        requestPlan.sort(Comparator.comparingInt(Benchmark.DelayedRequest::getTimeToAccept));
        return requestPlan;
    }

    private List<Integer> getNodes(int nNodes) {
        var nodes = new ArrayList<Integer>();
        for (int i = 0; i < nNodes; ++i) {
            int node = random.nextInt(graph.nNodes());
            nodes.add(node);
        }
        return nodes;
    }

    private List<GPS> getGPSList(List<Integer> nodes) {
        var gpsList = new ArrayList<GPS>();
        for (Integer node : nodes) {
            gpsList.add(graph.getGps(node));
        }
        return gpsList;
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

    public Benchmark getTaxiBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new TaxiSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getVSLSBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new VSLSSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getVSHSBenchmark() {
        var state = new SimpleORPInstance(graph, vehicleInitialGPS.stream()
                .map(gps -> new SimpleVehicle(gps, SimpleVehicle.State.PENDING, avgVehicleVelocity))
                .collect(Collectors.toList()));
        var solver = new VSHSSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getVSKTBenchmark() {
        var state = new VSKTORPInstance(graph);
        for (var gps : vehicleInitialGPS) {
            state.addVehicle(new VSKTVehicle(gps, Vehicle.State.PENDING, avgVehicleVelocity, (v) -> new ScheduleTree(v, state)));
        }
        var solver = new VSKTSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public Benchmark getVSHSKTBenchmark() {
        var state = new VSKTORPInstance(graph);
        for (var gps : vehicleInitialGPS) {
            state.addVehicle(new VSKTVehicle(gps, Vehicle.State.PENDING, avgVehicleVelocity, (v) -> new ScheduleTree(v, state)));
        }
        var solver = new VSHSKTSolver(state);
        var simulator = new Simulator(state, solver);
        return Benchmark.builder()
                .requestPlan(requestPlan)
                .simulator(simulator)
                .build();
    }

    public List<Benchmark> getBenchmarks() {
        return List.of(getTaxiBenchmark(), getVSLSBenchmark(), getVSHSBenchmark(), getVSKTBenchmark(), getVSHSKTBenchmark());
    }
}
