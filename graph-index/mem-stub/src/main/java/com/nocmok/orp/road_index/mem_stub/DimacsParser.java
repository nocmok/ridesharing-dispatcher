package com.nocmok.orp.road_index.mem_stub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DimacsParser {

    public List<String> readAllLines(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private String firstLineStartsWith(List<String> lines, String prefix) {
        return lines.stream()
                .filter(line -> line.startsWith(prefix)).findFirst()
                .orElse(null);
    }

    public List<double[]> readCo(InputStream co) throws IOException {
        var lines = readAllLines(co);
        var coordinates = new ArrayList<double[]>();
        for (var line : lines) {
            if (line.startsWith("v ")) {
                var lineSplit = line.split("\\s+");
                coordinates.add(new double[]{
                        Double.parseDouble(lineSplit[2]),
                        Double.parseDouble(lineSplit[3])
                });
            }
        }
        return coordinates;
    }

    public List<List<Edge>> readGr(InputStream gr) throws IOException {
        var lines = readAllLines(gr);
        String grInfo = firstLineStartsWith(lines, "p ");
        if (grInfo == null) {
            throw new RuntimeException("p header missed. cannot guess graph size");
        }
        int nNodes = Integer.parseInt(grInfo.split("\\s+")[2]);
        int nEdges = Integer.parseInt(grInfo.split("\\s+")[3]);
        var adjacencyList = new ArrayList<List<Edge>>();
        for (int i = 0; i < nNodes; ++i) {
            adjacencyList.add(new ArrayList<>());
        }
        for (var line : lines) {
            if (line.startsWith("a ")) {
                String[] lineSplit = line.split("\\s+");
                int from = Integer.parseInt(lineSplit[1]) - 1;
                int to = Integer.parseInt(lineSplit[2]) - 1;
                double distance = Double.parseDouble(lineSplit[3]);
                adjacencyList.get(from).add(new Edge(to, distance));
            }
        }
        return adjacencyList;
    }

    public void writeGr(OutputStream gr, List<List<Edge>> graph) throws IOException {
        int nEdges = 0;
        int nNodes = graph.size();
        for (var edges : graph) {
            nEdges += edges.size();
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(gr, StandardCharsets.UTF_8))) {
            writer.println("c this file is generated automatically by dimacs_tools software");
            writer.println("c ");
            writer.println("c graph contains " + nNodes + " nodes and " + nEdges + " arcs");
            writer.println("p sp " + nNodes + " " + nEdges);
            writer.println("c ");
            int nextNode = 1;
            for (var edges : graph) {
                for (var edge : edges) {
                    writer.println("a " + nextNode + " " + (edge.node + 1) + " " + edge.distance);
                }
                ++nextNode;
            }
        }
    }

    public void writeCo(OutputStream co, List<double[]> coordinates) {
        int nNodes = coordinates.size();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(co, StandardCharsets.UTF_8))) {
            writer.println("c this file is generated automatically by dimacs_tools software");
            writer.println("c ");
            writer.println("c graph contains " + nNodes + " nodes");
            writer.println("p aux sp co " + nNodes);
            writer.println("c ");
            int nextNode = 1;
            for (var coordinate : coordinates) {
                writer.println("v " + nextNode + " " + coordinate[0] + " " + coordinate[1]);
                ++nextNode;
            }
        }
    }

    public InmemoryGraph readGraph(InputStream gr, InputStream co) throws IOException {
        return new InmemoryGraph() {
            private final List<List<Edge>> adjacencyList = readGr(gr);
            private final List<double[]> coordinates = readCo(co);

            @Override public List<List<Edge>> adjacencyList() {
                return adjacencyList;
            }

            @Override public List<double[]> coordinates() {
                return coordinates;
            }
        };
    }
}