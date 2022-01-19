package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class ShortestPathSolver {

    private HashMap<Long, Route> cache = new HashMap<>();
    private Graph graph;

    public ShortestPathSolver(Graph graph) {
        this.graph = graph;
    }

    private Route _dijkstra(int startNode, int endNode) {
        double[] bestDistances = new double[graph.nNodes()];
        Arrays.fill(bestDistances, Double.POSITIVE_INFINITY);

        boolean[] used = new boolean[graph.nNodes()];
        var candidateNodes = new HashSet<Integer>();
        int[] prevNode = new int[graph.nNodes()];

        bestDistances[startNode] = 0;
        used[startNode] = true;
        prevNode[startNode] = startNode;

        for (var node : graph.getLinkedNodes(startNode)) {
            if (used[node]) {
                continue;
            }
            candidateNodes.add(node);
            bestDistances[node] = graph.getRoadCost(startNode, node);
            prevNode[node] = startNode;
        }

        for (int k = 2; k < graph.nNodes(); ++k) {
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
            for (var node : graph.getLinkedNodes(nextNode)) {
                if (used[node]) {
                    continue;
                }
                candidateNodes.add(node);
                if (bestDistances[nextNode] + graph.getRoadCost(nextNode, node) < bestDistances[node]) {
                    bestDistances[node] = bestDistances[nextNode] + graph.getRoadCost(nextNode, node);
                    prevNode[node] = nextNode;
                }
            }

            used[nextNode] = true;
            candidateNodes.remove(nextNode);
        }

        if (bestDistances[endNode] == Double.POSITIVE_INFINITY) {
            return new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        var route = new ArrayDeque<Integer>();
        int node = endNode;
        for (int i = 0; i < graph.nNodes(); ++i) {
            route.offerFirst(node);
            if (node == startNode) {
                break;
            }
            node = prevNode[node];
        }

        return new Route(new ArrayList<>(route), bestDistances[endNode]);
    }

    public Route dijkstra(int startNode, int endNode) {
        return cache.computeIfAbsent((((long)startNode << 32) | endNode), (k) -> _dijkstra(startNode, endNode));
    }
}
