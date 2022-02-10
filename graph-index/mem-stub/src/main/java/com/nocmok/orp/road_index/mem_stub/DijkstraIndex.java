package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphIndex;
import com.nocmok.orp.core_api.GraphIndexEntity;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoute;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DijkstraIndex implements GraphIndex {

    private HashMap<Long, GraphRoute> routesCache = new HashMap<>();
    private List<List<Edge>> gr;
    private List<double[]> co;
    private List<GCS> coGCS;
    private List<GraphIndexEntity> objects = new ArrayList<>();

    public DijkstraIndex(InmemoryGraph inmemoryGraph) {
        var graph = new InmemoryGraphOrpWrapper(inmemoryGraph);
        this.gr = graph.getAdjacencyList();
        this.co = graph.getCoordinates();
        this.coGCS = graph.getGCS();
    }

    private GraphRoute _dijkstra(int startNode, int endNode) {
        double[] bestDistances = new double[gr.size()];
        Arrays.fill(bestDistances, Double.POSITIVE_INFINITY);

        boolean[] used = new boolean[gr.size()];
        var candidateNodes = new HashSet<Integer>();
        int[] prevNode = new int[gr.size()];

        bestDistances[startNode] = 0;
        used[startNode] = true;
        prevNode[startNode] = startNode;

        for (var edge : gr.get(startNode)) {
            if (used[edge.node]) {
                continue;
            }
            candidateNodes.add(edge.node);
            bestDistances[edge.node] = edge.distance;
            prevNode[edge.node] = startNode;
        }

        for (int k = 2; k < gr.size(); ++k) {
            int nextNode = -1;
            double nextNodeDistance = Double.POSITIVE_INFINITY;

            for (var node : candidateNodes) {
                if (bestDistances[node] < nextNodeDistance) {
                    nextNode = node;
                    nextNodeDistance = bestDistances[node];
                }
            }

            // Граф несвязанный
            if (nextNode == -1) {
                break;
            }

            // Нашли нужную вершину
            if (nextNode == endNode) {
                break;
            }

            // Релаксация
            for (var edge : gr.get(nextNode)) {
                if (used[edge.node]) {
                    continue;
                }
                candidateNodes.add(edge.node);
                if (bestDistances[nextNode] + edge.distance < bestDistances[edge.node]) {
                    bestDistances[edge.node] = bestDistances[nextNode] + edge.distance;
                    prevNode[edge.node] = nextNode;
                }
            }

            used[nextNode] = true;
            candidateNodes.remove(nextNode);
        }

        if (bestDistances[endNode] == Double.POSITIVE_INFINITY) {
            return new GraphRoute(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        var route = new ArrayDeque<Integer>();
        int node = endNode;
        for (int i = 0; i < gr.size(); ++i) {
            route.offerFirst(node);
            if (node == startNode) {
                break;
            }
            node = prevNode[node];
        }

        return new GraphRoute(route.stream()
                .map(n -> new GraphNode(n, new GCS(co.get(n)[0], co.get(n)[1])))
                .collect(Collectors.toList()),
                bestDistances[endNode]);
    }

    private long hash(int startNode, int endNode) {
        return ((long) startNode << 32) | endNode;
    }

    private GraphRoute dijkstra(int startNode, int endNode) {
        return routesCache.computeIfAbsent(hash(startNode, endNode), (h) -> _dijkstra(startNode, endNode));
    }

    @Override public GraphRoute shortestRoute(int startNodeId, int endNodeId) {
        return dijkstra(startNodeId, endNodeId);
    }


    private GraphNode mapObjectToNode(GraphIndexEntity object) {
        return getClosestNode(object.getGcs());
    }

    @Override public List<GraphIndexEntity> getNeighborhood(GCS center, double radius) {
        var neighbors = new ArrayList<GraphIndexEntity>();
        var closestToCenterNode = getClosestNode(center);
        for (var object : objects) {
            var closestToVehicleNode = mapObjectToNode(object);
            var routeToVehicle = shortestRoute(closestToVehicleNode.getNodeId(), closestToCenterNode.getNodeId());
            double correction = distance(closestToCenterNode.getCoordinates(), center) + distance(closestToVehicleNode.getCoordinates(), object.getGcs());
            if (routeToVehicle.getCost() + correction > radius) {
                continue;
            }
            neighbors.add(object);
        }
        return neighbors;
    }

    private double distance(GCS a, GCS b) {
        return Math.hypot(a.lat() - b.lat(), a.lon() - b.lon());
    }

    private GraphNode getClosestNode(GCS gcs) {
        double bestDistance = Double.POSITIVE_INFINITY;
        int closestNode = -1;
        for (int i = 0; i < gr.size(); ++i) {
            double distance = distance(gcs, coGCS.get(i));
            if (distance < bestDistance) {
                closestNode = i;
                bestDistance = distance;
            }
        }
        return new GraphNode(closestNode, coGCS.get(closestNode));
    }

    public void addObject(GraphIndexEntity object) {
        objects.add(object);
    }

    public GCS getGCSByNodeId(int nodeId) {
        return coGCS.get(nodeId);
    }

}
