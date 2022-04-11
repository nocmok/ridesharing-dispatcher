package com.nocmok.orp.road_index.mem_stub.solver;

import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InMemoryGraph implements Graph {

    private final Map<String, Map<String, Link>> adjacencyList;
    private final Map<String, Node> nodes;

    public InMemoryGraph(Map<String, Map<String, Link>> adjacencyList, Map<String, Node> nodes) {
        this.adjacencyList = adjacencyList;
        this.nodes = nodes;
    }

    @Override public Collection<Node> getAllNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override public int nNodes() {
        return adjacencyList.size();
    }

    @Override public boolean containsNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }

    @Override public Node getNodeMetadata(String nodeId) {
        return nodes.get(nodeId);
    }

    @Override
    public Map<String, Link> getOutboundLinksMap(String nodeId) {
        return adjacencyList.get(nodeId);
    }
}
