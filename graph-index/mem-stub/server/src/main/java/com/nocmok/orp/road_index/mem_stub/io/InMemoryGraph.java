package com.nocmok.orp.road_index.mem_stub.io;

import com.nocmok.orp.road_index.mem_stub.solver.Graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGraph implements Graph {

    private final Map<String, Map<String, Link>> adjacencyList;
    private final Map<String, Node> nodes;

    public InMemoryGraph(Map<String, Map<String, Link>> adjacencyList, Map<String, Node> nodes) {
        this.adjacencyList = new ConcurrentHashMap<>(adjacencyList);
        this.nodes = new ConcurrentHashMap<>(nodes);
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
        return Objects.requireNonNullElse(adjacencyList.get(nodeId), Collections.emptyMap());
    }
}
