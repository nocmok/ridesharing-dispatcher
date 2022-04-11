package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GraphMetadataStorageImpl implements SpatialGraphMetadataStorage {

    private final Graph graph;

    public GraphMetadataStorageImpl(Graph graph) {
        this.graph = graph;
    }

    private Node mapInternalNodeToGraphApiNode(Graph.Node nodeMetadata) {
        return new com.nocmok.orp.graph.api.Node(
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

    @Override public Segment getSegment(String startNodeId, String endNodeId) {
        return mapInternalLinkToGraphApiSegment(graph.getOutboundLinksMap(startNodeId).get(endNodeId));
    }

    @Override public Node getNode(String id) {
        return mapInternalNodeToGraphApiNode(graph.getNodeMetadata(id));
    }

    @Override public List<Segment> getSegments(List<String> startNodeIds, List<String> endNodeIds) {
        if (startNodeIds.size() != endNodeIds.size()) {
            throw new IllegalArgumentException("identifiers lists sizes mismatched");
        }

        var segments = new ArrayList<Segment>();

        var startIdsIt = startNodeIds.iterator();
        var endIdsIt = endNodeIds.iterator();

        while (startIdsIt.hasNext() && endIdsIt.hasNext()) {
            segments.add(mapInternalLinkToGraphApiSegment(graph.getOutboundLinksMap(startIdsIt.next()).get(endIdsIt.next())));
        }

        return Collections.unmodifiableList(segments);
    }

    @Override public List<Node> getNodes(List<String> ids) {
        return ids.stream()
                .map(graph::getNodeMetadata)
                .map(this::mapInternalNodeToGraphApiNode)
                .collect(Collectors.toUnmodifiableList());
    }
}
