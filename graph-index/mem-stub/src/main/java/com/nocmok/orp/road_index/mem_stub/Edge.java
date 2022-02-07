package com.nocmok.orp.road_index.mem_stub;

public class Edge {

    public int node;
    public double distance;

    public Edge(int node, double distance) {
        this.node = node;
        this.distance = distance;
    }

    @Override public String toString() {
        return "[" + node + "," + distance + "]";
    }
}
