package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class InmemoryGraphOrpWrapper {

    private final InmemoryGraph inmemoryGraph;
    private final List<GCS> coGCS;
    private final List<GraphRoad> roads;
    private final List<GraphNode> graphNodes;

    public InmemoryGraphOrpWrapper(InmemoryGraph inmemoryGraph) {
        this.inmemoryGraph = inmemoryGraph;
        this.coGCS = inmemoryGraph.coordinates().stream()
                .map(c -> new GCS(c[0], c[1]))
                .collect(Collectors.toCollection(ArrayList::new));
        this.graphNodes = IntStream.range(0, coGCS.size())
                .mapToObj(nodeId -> new GraphNode(nodeId, coGCS.get(nodeId)))
                .collect(Collectors.toList());
        this.roads = new ArrayList<>();
        for (int startNode = 0; startNode < inmemoryGraph.adjacencyList().size(); ++startNode) {
            for (Edge road : inmemoryGraph.adjacencyList().get(startNode)) {
                roads.add(new GraphRoad(
                        graphNodes.get(startNode),
                        graphNodes.get(road.node),
                        road.distance
                ));
            }
        }
    }

    public List<List<Edge>> getAdjacencyList() {
        return inmemoryGraph.adjacencyList();
    }

    public List<double[]> getCoordinates() {
        return inmemoryGraph.coordinates();
    }

    public List<GraphRoad> getRoads() {
        return roads;
    }

    public List<GCS> getGCS() {
        return coGCS;
    }

    public List<GraphNode> getGraphNodes() {
        return graphNodes;
    }
}
