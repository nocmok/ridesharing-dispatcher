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
import java.util.List;
import java.util.Random;

public class BenchmarkLauncher {

    private static Graph GRAPH;

    private static void runBenchmark(String solver, Benchmark benchmark) {
        long start;
        long stop;
        System.out.println("orp solver=" + solver);
        start = System.currentTimeMillis();
        System.out.println(benchmark.runBenchmarking());
        stop = System.currentTimeMillis();
        System.out.println("elapsed=" + ((stop - start) / 1000) + " (s)");
    }

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

    // algo,sample,distance_savings
    private static void distanceSavingsPerSampleBenchmark(OutputStream out) throws IOException {
        Random random = new Random(1000);
        int nSamples = 10;
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo", "sample", "distance_savings"));
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
                    writer.write(getCSVLine(benchmark.getSimulator().getSolver().getClass().getSimpleName(), sample, metrics.getDistanceSavings()));
                    writer.write("\n");
                }

            }
        }
    }

    // algo,sample,avg,min,max
    private static void processingTimePerSampleBenchmark(OutputStream out) throws IOException {
        Random random = new Random(1000);
        int nSamples = 10;
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo", "sample", "avg", "min", "max"));
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
                            metrics.getProcessingTimePerRequestExpectation(),
                            metrics.getProcessingTimePerRequestMinimum(),
                            metrics.getProcessingTimePerRequestMaximum()));
                    writer.write("\n");
                }

            }
        }
    }

    // algo, sample, nVehicles, service_rate
    private static void serviceRatePerVehiclesBenchmark(OutputStream out) throws IOException {
        int nSamples = 10;
        List<Integer> nVehicles = List.of(10, 100, 1000);
        try (var writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out), StandardCharsets.UTF_8))) {
            writer.write(getCSVLine("algo", "sample", "nVehicles", "service_rate"));
            writer.write("\n");
            for (int nVehicle : nVehicles) {
                Random random = new Random(1000);
                for (int sample = 0; sample < nSamples; ++sample) {
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
                                sample,
                                nVehicle,
                                metrics.getServiceRate()));
                        writer.write("\n");
                    }
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
        distanceSavingsPerSampleBenchmark(new FileOutputStream("ny131_distance_savings.csv"));
        processingTimePerSampleBenchmark(new FileOutputStream("ny131_processing_time.csv"));
        serviceRatePerVehiclesBenchmark(new FileOutputStream("131_service_rate.csv"));
    }
}
