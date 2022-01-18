package com.nocmok.orp.proto.graph;

import com.nocmok.orp.proto.pojo.GPS;

public interface Graph {

    int nNodes();

    GPS getGps(int node);

    Iterable<Integer> getLinkedNodes(int node);

    double getRoadCost(int startNode, int endNode);

}
