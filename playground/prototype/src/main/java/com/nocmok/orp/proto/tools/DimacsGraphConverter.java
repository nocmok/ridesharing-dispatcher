package com.nocmok.orp.proto.tools;

import com.nocmok.orp.proto.graph.DumbGraph;
import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;

import java.util.ArrayList;
import java.util.List;

public class DimacsGraphConverter {

    public Graph convert(List<List<DimacsParser.Edge>> dimacsGraph, List<double[]> dimacsCoordinates) {
        var topology = new ArrayList<List<Integer>>();
        for (var edges : dimacsGraph) {
            var nodes = new ArrayList<Integer>();
            for (var edge : edges) {
                nodes.add(edge.node);
            }
            topology.add(nodes);
        }
        var geometry = new ArrayList<GPS>();
        for (var coordinate : dimacsCoordinates) {
            geometry.add(new GPS(coordinate[0], coordinate[1]));
        }
        return new DumbGraph(topology, geometry);
    }
}
