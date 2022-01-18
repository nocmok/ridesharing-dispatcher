package com.nocmok.dimacs_tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Модуль с алгоритмами для получения подвыборок графа
public class GraphReducer {

    // Возвращает окрестность вершины графа с заданным радиусом.
    // Подграф возвращается в виде списка вершин на которых он должен быть построен
    public List<Integer> getNeighborhood(List<List<DimacsParser.Edge>> graph, int center, int radius) {
        var neighborhood = new ArrayList<Integer>();
        var queue = new ArrayDeque<Integer>();
        var used = new HashSet<Integer>();

        queue.offerLast(center);
        neighborhood.add(center);
        used.add(center);

        for (int i = 0; i < radius; ++i) {
            if (queue.isEmpty()) {
                break;
            }
            var currentNode = queue.pollFirst();
            for (var edge : graph.get(currentNode)) {
                if (used.contains(edge.node)) {
                    continue;
                }
                neighborhood.add(edge.node);
                queue.offerLast(edge.node);
                used.add(edge.node);
            }
        }

        return neighborhood;
    }

    // Возвращает подграф по списку вершин
    public List<List<DimacsParser.Edge>> getSubgraph(List<List<DimacsParser.Edge>> graph, List<Integer> subgraphNodes) {
        var encoding = new HashMap<Integer, Integer>();
        int code = 0;
        for (var node : subgraphNodes) {
            encoding.put(node, code);
            ++code;
        }

        var subGraph = new ArrayList<List<DimacsParser.Edge>>();
        for (int i = 0; i < subgraphNodes.size(); ++i) {
            subGraph.add(new ArrayList<>());
        }

        int nodeCode = 0;
        for (var node : subgraphNodes) {
            for (var edge : graph.get(node)) {
                if (!encoding.containsKey(edge.node)) {
                    continue;
                }
                subGraph.get(nodeCode).add(new DimacsParser.Edge(encoding.get(edge.node), edge.distance));
            }
            ++nodeCode;
        }

        return subGraph;
    }

    // Возвращает список координат по списку вершин
    public List<double[]> getSubgraphCoordinates(List<double[]> coordinates, List<Integer> subgraphNodes) {
        var result = new ArrayList<double[]>();
        for (var node : subgraphNodes) {
            result.add(coordinates.get(node));
        }
        return result;
    }

    // Возвращает подграф из nNodes вершин, который содержит максимальное количество ребер.
    // Подграф возвращается в виде списка вершин на которых он строится.
    public List<Integer> getDenseSubgraph(List<List<DimacsParser.Edge>> graph, int nNodes) {
        int[] degrees = new int[graph.size()];
        boolean[] deleted = new boolean[graph.size()];

        for (int node = 0; node < graph.size(); ++node) {
            for (var edge : graph.get(node)) {
                ++degrees[node];
                ++degrees[edge.node];
            }
        }

        var adjacentNodes = new ArrayList<Set<Integer>>();
        for (int i = 0; i < graph.size(); ++i) {
            adjacentNodes.add(new HashSet<Integer>());
        }
        for (int startNode = 0; startNode < graph.size(); ++startNode) {
            for (var edge : graph.get(startNode)) {
                adjacentNodes.get(startNode).add(edge.node);
                adjacentNodes.get(edge.node).add(startNode);
            }
        }

        for (int k = 0; k < (graph.size() - nNodes); ++k) {
            // найти вершину с минимальной степенью
            int minNode = -1;
            for (int node = 0; node < degrees.length; ++node) {
                // пропускаем уже удаленные вершины
                if (deleted[node]) {
                    continue;
                }
                if (minNode == -1 || degrees[node] < degrees[minNode]) {
                    minNode = node;
                }
            }

            for (var node : adjacentNodes.get(minNode)) {
                if (deleted[node]) {
                    continue;
                }
                --degrees[node];
            }

            deleted[minNode] = true;
        }

        var subgraph = new ArrayList<Integer>();

        for (int node = 0; node < deleted.length; ++node) {
            if (!deleted[node]) {
                subgraph.add(node);
            }
        }

        return subgraph;
    }
}
