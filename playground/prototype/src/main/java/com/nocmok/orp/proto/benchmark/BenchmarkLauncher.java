package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class BenchmarkLauncher {

    private static Graph GRAPH;

    private static String getCSVLine(Object... values) {
        StringBuilder line = new StringBuilder();
        for (var value : values) {
            line.append(value);
            line.append(",");
        }
        if (!line.isEmpty()) {
            line.deleteCharAt(line.length() - 1);
        }
        return line.toString();
    }

    private static void metricsPerSampleBenchmark(OutputStream out) throws IOException {
        Random random = new Random(1000);
        int nSamples = 10;
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine(
                    "algo",
                    "sample",
                    "service_rate",
                    "total_requests",
                    "denied_requests",
                    "processing_time_avg",
                    "processing_time_min",
                    "processing_time_max",
                    "total_travelled_distance",
                    "distance_savings",
                    "distance_savings_meters",
                    "effective_distance",
                    "accepted_effective_distance",
                    "denied_effective_distance",
                    "combined_distance_meters",
                    "combined_distance_percentage"
            ));
            writer.write("\n");

            for (int sample = 0; sample < nSamples; ++sample) {
                var benchmarking = Benchmarking.builder()
                        .random(random)
                        .avgVehicleVelocity(40)
                        .nIterations(86_400)
                        .nRequests(1000)
                        .nVehicles(50)
                        .graph(GRAPH)
                        .maxClientWaitingTimeSeconds(480)
                        .maxRidesharingLagSeconds(480)
                        .vehicleCapacity(3)
                        .build();

                for (var benchmark : benchmarking.getBenchmarks()) {
                    System.out.println(benchmark.getSimulator().getSolver());
                    var metrics = benchmark.runBenchmarking();
                    System.out.println(metrics);
                    System.out.println();
                    writer.write(getCSVLine(benchmark.getSimulator().getSolver().getClass().getSimpleName(),
                            sample,
                            metrics.getServiceRate(),
                            metrics.getTotalRequests(),
                            metrics.getDeniedRequests(),
                            metrics.getProcessingTimePerRequestExpectation(),
                            metrics.getProcessingTimePerRequestMinimum(),
                            metrics.getProcessingTimePerRequestMaximum(),
                            metrics.getTotalTravelledDistance(),
                            metrics.getDistanceSavings(),
                            metrics.getDistanceSavingsMeters(),
                            metrics.getEffectiveDistance(),
                            metrics.getAcceptedRequestsEffectiveDistance(),
                            metrics.getDeniedRequestsEffectiveDistance(),
                            metrics.getCombinedDistance(),
                            metrics.getCombinedDistancePercentage()
                    ));
                    writer.write("\n");
                }

            }
        }
    }

    private static void metricsPerNVehiclesBenchmark(OutputStream out) throws IOException {
        int step = 100;
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo",
                    "n_vehicles",
                    "service_rate",
                    "total_requests",
                    "denied_requests",
                    "processing_time_avg",
                    "processing_time_min",
                    "processing_time_max",
                    "total_travelled_distance",
                    "distance_savings",
                    "distance_savings_meters",
                    "effective_distance",
                    "accepted_effective_distance",
                    "denied_effective_distance",
                    "combined_distance_meters",
                    "combined_distance_percentage"));
            writer.write("\n");
            for (int nVehicle = 100; nVehicle < 1000; nVehicle += step) {
                Random random = new Random(1000);
                var benchmarking = Benchmarking.builder()
                        .random(random)
                        .avgVehicleVelocity(40)
                        .nIterations(86_400)
                        .nRequests(1000)
                        .nVehicles(nVehicle)
                        .graph(GRAPH)
                        .maxClientWaitingTimeSeconds(480)
                        .maxRidesharingLagSeconds(480)
                        .vehicleCapacity(3)
                        .build();

                for (var benchmark : benchmarking.getBenchmarks()) {
                    System.out.println(benchmark.getSimulator().getSolver());
                    var metrics = benchmark.runBenchmarking();
                    System.out.println(metrics);
                    System.out.println();
                    writer.write(getCSVLine(benchmark.getSimulator().getSolver().getClass().getSimpleName(),
                            nVehicle,
                            metrics.getServiceRate(),
                            metrics.getTotalRequests(),
                            metrics.getDeniedRequests(),
                            metrics.getProcessingTimePerRequestExpectation(),
                            metrics.getProcessingTimePerRequestMinimum(),
                            metrics.getProcessingTimePerRequestMaximum(),
                            metrics.getTotalTravelledDistance(),
                            metrics.getDistanceSavings(),
                            metrics.getDistanceSavingsMeters(),
                            metrics.getEffectiveDistance(),
                            metrics.getAcceptedRequestsEffectiveDistance(),
                            metrics.getDeniedRequestsEffectiveDistance(),
                            metrics.getCombinedDistance(),
                            metrics.getCombinedDistancePercentage()
                    ));
                    writer.write("\n");
                }
            }
        }
    }

    private static void metricsPerTimeLagBenchmark(OutputStream out) throws IOException {
        int step = 100; // 0, 1000
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo",
                    "max_time_lag",
                    "service_rate",
                    "total_requests",
                    "denied_requests",
                    "processing_time_avg",
                    "processing_time_min",
                    "processing_time_max",
                    "total_travelled_distance",
                    "distance_savings",
                    "distance_savings_meters",
                    "effective_distance",
                    "accepted_effective_distance",
                    "denied_effective_distance",
                    "combined_distance_meters",
                    "combined_distance_percentage"));
            writer.write("\n");
            for (int timeLag = 0; timeLag < 1000; timeLag += step) {
                Random random = new Random(1000);
                var benchmarking = Benchmarking.builder()
                        .random(random)
                        .avgVehicleVelocity(40)
                        .nIterations(86_400)
                        .nRequests(1000)
                        .nVehicles(50)
                        .graph(GRAPH)
                        .maxClientWaitingTimeSeconds(480)
                        .maxRidesharingLagSeconds(timeLag)
                        .vehicleCapacity(3)
                        .build();

                for (var benchmark : benchmarking.getBenchmarks()) {
                    System.out.println(benchmark.getSimulator().getSolver());
                    var metrics = benchmark.runBenchmarking();
                    System.out.println(metrics);
                    System.out.println();
                    writer.write(getCSVLine(benchmark.getSimulator().getSolver().getClass().getSimpleName(),
                            timeLag,
                            metrics.getServiceRate(),
                            metrics.getTotalRequests(),
                            metrics.getDeniedRequests(),
                            metrics.getProcessingTimePerRequestExpectation(),
                            metrics.getProcessingTimePerRequestMinimum(),
                            metrics.getProcessingTimePerRequestMaximum(),
                            metrics.getTotalTravelledDistance(),
                            metrics.getDistanceSavings(),
                            metrics.getDistanceSavingsMeters(),
                            metrics.getEffectiveDistance(),
                            metrics.getAcceptedRequestsEffectiveDistance(),
                            metrics.getDeniedRequestsEffectiveDistance(),
                            metrics.getCombinedDistance(),
                            metrics.getCombinedDistancePercentage()
                    ));
                    writer.write("\n");
                }
            }
        }
    }

    private static void metricsPerCapacityBenchmark(OutputStream out) throws IOException {
        int step = 1; // 0, 10
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo",
                    "capacity",
                    "service_rate",
                    "total_requests",
                    "denied_requests",
                    "processing_time_avg",
                    "processing_time_min",
                    "processing_time_max",
                    "total_travelled_distance",
                    "distance_savings",
                    "distance_savings_meters",
                    "effective_distance",
                    "accepted_effective_distance",
                    "denied_effective_distance",
                    "combined_distance_meters",
                    "combined_distance_percentage"));
            writer.write("\n");
            for (int capacity = 1; capacity < 10; capacity += step) {
                Random random = new Random(1000);
                var benchmarking = Benchmarking.builder()
                        .random(random)
                        .avgVehicleVelocity(40)
                        .nIterations(86_400)
                        .nRequests(1000)
                        .nVehicles(50)
                        .graph(GRAPH)
                        .maxClientWaitingTimeSeconds(480)
                        .maxRidesharingLagSeconds(480)
                        .vehicleCapacity(capacity)
                        .build();

                for (var benchmark : benchmarking.getBenchmarks()) {
                    System.out.println(benchmark.getSimulator().getSolver());
                    var metrics = benchmark.runBenchmarking();
                    System.out.println(metrics);
                    System.out.println();
                    writer.write(getCSVLine(benchmark.getSimulator().getSolver().getClass().getSimpleName(),
                            capacity,
                            metrics.getServiceRate(),
                            metrics.getTotalRequests(),
                            metrics.getDeniedRequests(),
                            metrics.getProcessingTimePerRequestExpectation(),
                            metrics.getProcessingTimePerRequestMinimum(),
                            metrics.getProcessingTimePerRequestMaximum(),
                            metrics.getTotalTravelledDistance(),
                            metrics.getDistanceSavings(),
                            metrics.getDistanceSavingsMeters(),
                            metrics.getEffectiveDistance(),
                            metrics.getAcceptedRequestsEffectiveDistance(),
                            metrics.getDeniedRequestsEffectiveDistance(),
                            metrics.getCombinedDistance(),
                            metrics.getCombinedDistancePercentage()
                    ));
                    writer.write("\n");
                }
            }
        }
    }

    private static Graph loadGraph() {
        try {
            var dimacsParser = new DimacsParser();
            var gr = dimacsParser.readGr(Benchmark.class.getClassLoader().getResourceAsStream("ny131.gr"));
            var co = dimacsParser.readCo(Benchmark.class.getClassLoader().getResourceAsStream("ny131.co"));
            return new DimacsGraphConverter().convert(gr, co);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        GRAPH = loadGraph();
//        metricsPerSampleBenchmark(new FileOutputStream("ny131_metrics_per_sample.csv"));
        metricsPerNVehiclesBenchmark(new FileOutputStream("ny131_metrics_per_n_vehicles.csv"));
        metricsPerTimeLagBenchmark(new FileOutputStream("ny131_metrics_per_time_lag.csv"));
        metricsPerCapacityBenchmark(new FileOutputStream("ny131_metrics_per_capacity.csv"));
    }
}
