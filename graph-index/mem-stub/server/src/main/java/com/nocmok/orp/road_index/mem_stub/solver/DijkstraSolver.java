package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.ShortestRouteSolver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class DijkstraSolver implements ShortestRouteSolver {

    private Graph graph;

    public DijkstraSolver(Graph graph) {
        this.graph = graph;
    }

    private Node mapInternalNodeToGraphApiNode(Graph.Node nodeMetadata) {
        return new Node(
                nodeMetadata.getId(),
                nodeMetadata.getLatitude(),
                nodeMetadata.getLongitude()
        );
    }

    private Segment mapInternalLinkToGraphApiSegment(Graph.Link link) {
        return new Segment(link.getId(),
                mapInternalNodeToGraphApiNode(graph.getNodeMetadata(link.getStartNodeId())),
                mapInternalNodeToGraphApiNode(graph.getNodeMetadata(link.getEndNodeId())),
                link.getCost());
    }

    private Route mapSegmentsToRoute(List<Segment> segments) {
        if (segments.isEmpty()) {
            return new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        double routeCost = segments.stream().map(Segment::getCost).reduce(0d, Double::sum);
        return new Route(segments, routeCost);
    }

    private List<Graph.Link> dijkstra(String originNodeId, String destinationNodeId) {
        // Список вершин до которых известно минимальное расстояние
        var processedNodes = new HashSet<String>();
        var nodesToProcess = new HashSet<String>();
        // Текущие известные минимальные расстояния
        var knownShortestDistances = new HashMap<String, Double>();
        // Для восстановления кратчайшего пути
        var predecessors = new HashMap<String, Graph.Link>();

        knownShortestDistances.put(originNodeId, 0d);
        processedNodes.add(originNodeId);
        predecessors.put(originNodeId, null);

        for (var link : graph.getOutboundLinksMap(originNodeId).values()) {
            knownShortestDistances.put(link.getEndNodeId(), link.getCost());
            predecessors.put(link.getEndNodeId(), link);
            nodesToProcess.add(link.getEndNodeId());
        }

        for (int i = 0; i + 1 < graph.nNodes(); ++i) {

            double closestNodeDistance = Double.POSITIVE_INFINITY;
            String closestNode = null;

            for (var node : nodesToProcess) {
                double nodeDistance = knownShortestDistances.get(node);
                if (nodeDistance < closestNodeDistance) {
                    closestNodeDistance = nodeDistance;
                    closestNode = node;
                }
            }

            // Уже обошли всю компоненту связности
            if (closestNode == null) {
                break;
            }

            nodesToProcess.remove(closestNode);
            processedNodes.add(closestNode);

            if (Objects.equals(closestNode, destinationNodeId)) {
                break;
            }

            // Релаксация
            for (var link : graph.getOutboundLinksMap(closestNode).values()) {
                if (processedNodes.contains(link.getEndNodeId())) {
                    continue;
                }
                nodesToProcess.add(link.getEndNodeId());
                double relaxedDistance = closestNodeDistance + link.getCost();
                if (relaxedDistance < knownShortestDistances.getOrDefault(link.getEndNodeId(), Double.POSITIVE_INFINITY)) {
                    knownShortestDistances.put(link.getEndNodeId(), relaxedDistance);
                    predecessors.put(link.getEndNodeId(), link);
                }
            }
        }

        var route = new ArrayDeque<Graph.Link>();
        var node = destinationNodeId;

        for (int i = 0; i < graph.nNodes(); ++i) {
            var link = predecessors.get(node);
            if (link == null) {
                break;
            }
            route.offerFirst(link);
            node = link.getStartNodeId();
        }

        return new ArrayList<>(route);
    }

    /**
     * @return Если не существует пути между указанными вершинами возвращает пустой список.
     */
    @Override public Route getShortestRoute(String originNodeId, String destinationNodeId) {
        if (!graph.containsNode(originNodeId)) {
            throw new NoSuchElementException("node with id " + originNodeId + " doesn't present in graph");
        }
        if (!graph.containsNode(destinationNodeId)) {
            throw new NoSuchElementException("node with id " + destinationNodeId + " doesn't present in graph");
        }
        return mapSegmentsToRoute(dijkstra(originNodeId, destinationNodeId)
                .stream()
                .map(this::mapInternalLinkToGraphApiSegment)
                .collect(Collectors.toList()));
    }
}
