package com.nocmok.orp.road_index.mem_stub.io;

import com.nocmok.orp.road_index.mem_stub.solver.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DimacsReader implements GraphReader {

    @Override public InMemoryGraph readGraph(File... files) {
        var coFile = Arrays.stream(files).filter(file -> file.getName().matches(".*\\.co")).findFirst();
        var grFile = Arrays.stream(files).filter(file -> file.getName().matches(".*\\.gr")).findFirst();
        if (coFile.isEmpty()) {
            throw new RuntimeException(".co file missed");
        }
        if (grFile.isEmpty()) {
            throw new RuntimeException(".gr file missed");
        }
        try {
            var nodes = readCo(new FileInputStream(coFile.get()));
            var links = readGr(new FileInputStream(grFile.get()));
            return new InMemoryGraph(links, nodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readAllLines(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private String firstLineStartsWith(List<String> lines, String prefix) {
        return lines.stream()
                .filter(line -> line.startsWith(prefix)).findFirst()
                .orElse(null);
    }

    private Map<String, Graph.Node> readCo(InputStream co) throws IOException {
        var lines = readAllLines(co);
        var coordinates = new HashMap<String, Graph.Node>();
        for (var line : lines) {
            if (line.startsWith("v ")) {
                var lineSplit = line.split("\\s+");
                String nodeId = Integer.toString(Integer.parseInt(lineSplit[1]) - 1);
                coordinates.put(nodeId, new Graph.Node(
                                nodeId,
                                Double.parseDouble(lineSplit[2]),
                                Double.parseDouble(lineSplit[3])
                        )
                );
            }
        }
        return coordinates;
    }

    private Map<String, Map<String, Graph.Link>> readGr(InputStream gr) throws IOException {
        var lines = readAllLines(gr);
        String grInfo = firstLineStartsWith(lines, "p ");
        if (grInfo == null) {
            throw new RuntimeException("p header missed. cannot guess graph size");
        }
        int nNodes = Integer.parseInt(grInfo.split("\\s+")[2]);
        int nEdges = Integer.parseInt(grInfo.split("\\s+")[3]);
        var adjacencyList = new HashMap<String, Map<String, Graph.Link>>();

        for (int i = 0; i < nNodes; ++i) {
            adjacencyList.put(Integer.toString(i), new HashMap<>());
        }

        for (var line : lines) {
            if (line.startsWith("a ")) {
                String[] lineSplit = line.split("\\s+");
                int from = Integer.parseInt(lineSplit[1]) - 1;
                int to = Integer.parseInt(lineSplit[2]) - 1;
                double cost = Double.parseDouble(lineSplit[3]);
                adjacencyList.get(Integer.toString(from)).put(
                        Integer.toString(to),
                        new Graph.Link(
                                from + ":" + to,
                                Integer.toString(from),
                                Integer.toString(to),
                                cost
                        )
                );
            }
        }

        return adjacencyList;
    }
}
