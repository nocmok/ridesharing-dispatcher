package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.RoadIndex;
import com.nocmok.orp.core_api.RoadIndexEntity;
import com.nocmok.orp.core_api.RoadNode;
import com.nocmok.orp.core_api.RoadRoute;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DijkstraIndex implements RoadIndex {

    private DimacsParser dimacsParser = new DimacsParser();
    private HashMap<Long, RoadRoute> routesCache = new HashMap<>();
    private List<List<Edge>> gr;
    private List<double[]> co;
    private List<GCS> coGCS;
    private List<RoadIndexEntity> objects = new ArrayList<>();

    public DijkstraIndex(InputStream grIn, InputStream coIn) {
        try {
            this.gr = dimacsParser.readGr(grIn);
            this.co = dimacsParser.readCo(coIn);
            this.coGCS = co.stream()
                    .map(c -> new GCS(c[0], c[1]))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RoadRoute _dijkstra(int startNode, int endNode) {
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
            return new RoadRoute(Collections.emptyList(), Double.POSITIVE_INFINITY);
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

        return new RoadRoute(route.stream()
                .map(n -> new RoadNode(n, new GCS(co.get(n)[0], co.get(n)[1])))
                .collect(Collectors.toList()),
                bestDistances[endNode]);
    }

    private long hash(int startNode, int endNode) {
        return ((long) startNode << 32) | endNode;
    }

    private RoadRoute dijkstra(int startNode, int endNode) {
        return routesCache.computeIfAbsent(hash(startNode, endNode), (h) -> _dijkstra(startNode, endNode));
    }

    @Override public RoadRoute shortestRoute(int startNodeId, int endNodeId) {
        return dijkstra(startNodeId, endNodeId);
    }


    private RoadNode mapObjectToNode(RoadIndexEntity object) {
        return getClosestNode(object.getGcs());
    }

    @Override public List<RoadIndexEntity> getNeighborhood(GCS center, double radius) {
        var neighbors = new ArrayList<RoadIndexEntity>();
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

    private RoadNode getClosestNode(GCS gcs) {
        double bestDistance = Double.POSITIVE_INFINITY;
        int closestNode = -1;
        for (int i = 0; i < gr.size(); ++i) {
            double distance = distance(gcs, coGCS.get(i));
            if (distance < bestDistance) {
                closestNode = i;
                bestDistance = distance;
            }
        }
        return new RoadNode(closestNode, coGCS.get(closestNode));
    }

    public void addObject(RoadIndexEntity object) {
        objects.add(object);
    }

    public GCS getGCSByNodeId(int nodeId) {
        return coGCS.get(nodeId);
    }
}
