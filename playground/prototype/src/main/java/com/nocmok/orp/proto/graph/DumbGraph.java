package com.nocmok.orp.proto.graph;

import com.nocmok.orp.proto.pojo.GPS;

import java.util.List;

public class DumbGraph implements Graph {

    private List<List<Integer>> topology;
    private List<GPS> geometry;

    public DumbGraph(List<List<Integer>> topology, List<GPS> geometry) {
        this.topology = topology;
        this.geometry = geometry;
    }

    @Override public int nNodes() {
        return topology.size();
    }

    @Override public GPS getGps(int node) {
        return geometry.get(node);
    }

    @Override public Iterable<Integer> getLinkedNodes(int node) {
        return topology.get(node);
    }

    @Override public double getRoadCost(int startNode, int endNode) {
        return Math.hypot(geometry.get(startNode).x - geometry.get(endNode).x,
                geometry.get(startNode).y - geometry.get(endNode).y);
    }
}
