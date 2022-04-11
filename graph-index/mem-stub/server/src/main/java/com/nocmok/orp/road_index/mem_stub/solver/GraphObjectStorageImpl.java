package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GraphObjectStorageImpl implements SpatialGraphObjectsStorage {

    private final Graph graph;
    private final Map<String, SpatialGraphObject> graphObjectRegistry;
    private final DijkstraSolver dijkstraSolver;

    public GraphObjectStorageImpl(Graph graph) {
        this.graph = graph;
        this.graphObjectRegistry = new HashMap<>();
        this.dijkstraSolver = new DijkstraSolver(graph);
    }

    @Override public Optional<SpatialGraphObject> getObject(String id) {
        return Optional.ofNullable(graphObjectRegistry.get(id));
    }

    @Override public List<SpatialGraphObject> getNeighborhood(String nodeId, double radius) {
        var neighborhood = new ArrayList<SpatialGraphObject>();
        for (var obj : graphObjectRegistry.values()) {
            if (dijkstraSolver.getShortestRoute(nodeId, obj.getSegment().getEndNode().getId()).getRouteCost() <= radius) {
                neighborhood.add(obj);
            }
        }
        return neighborhood;
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

    @Override public void updateObject(ObjectUpdater objectUpdater) {
        var graphObject = graphObjectRegistry.computeIfAbsent(objectUpdater.getId(), (objectId) -> new SpatialGraphObject(objectId, null, null, null));
        graphObject.setSegment(
                mapInternalLinkToGraphApiSegment(graph.getOutboundLinksMap(objectUpdater.getSegmentStartNodeId()).get(objectUpdater.getSegmentEndNodeId())));
        graphObject.setLatitude(objectUpdater.getLatitude());
        graphObject.setLongitude(objectUpdater.getLongitude());
    }

    @Override public void removeObject(String id) {
        graphObjectRegistry.remove(id);
    }
}
