package com.nocmok.dimacs_tools;

import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

    private static void createSubgraph() throws IOException {
        var dimacsParser = new DimacsParser();
        var gr = dimacsParser.readGr(Test.class.getClassLoader().getResourceAsStream("USA-road-d.NY.gr"));
        var co = dimacsParser.readCo(Test.class.getClassLoader().getResourceAsStream("USA-road-d.NY.co"));

        int node = 53124;
        int radius = 1000;

        var reducer = new GraphReducer();
        var subgraphNodes = reducer.getNeighborhood(gr, node, radius);
        var grReduced = reducer.getSubgraph(gr, subgraphNodes);
        var coReduced = reducer.getSubgraphCoordinates(co, subgraphNodes);

        int nEdges = 0;
        int nNodes = subgraphNodes.size();

        for(var edges : grReduced) {
            nEdges += edges.size();
        }

        System.out.println("nNodes=" + nNodes);
        System.out.println("nEdges=" + nEdges);

        dimacsParser.writeGr(new FileOutputStream("USA-road-d.NY.REDUCED.gr"), grReduced);
        dimacsParser.writeCo(new FileOutputStream("USA-road-d.NY.REDUCED.co"), coReduced);
    }

    private static void createDenseSubgraph() throws IOException {
        var dimacsParser = new DimacsParser();
        var gr = dimacsParser.readGr(Test.class.getClassLoader().getResourceAsStream("USA-road-d.NY.REDUCED.gr"));
        var co = dimacsParser.readCo(Test.class.getClassLoader().getResourceAsStream("USA-road-d.NY.REDUCED.co"));

        var reducer = new GraphReducer();
        var subgraphNodes = reducer.getDenseSubgraph(gr, 34);
        var grReduced = reducer.getSubgraph(gr, subgraphNodes);
        var coReduced = reducer.getSubgraphCoordinates(co, subgraphNodes);

        int nEdges = 0;
        int nNodes = subgraphNodes.size();

        for(var edges : grReduced) {
            nEdges += edges.size();
        }

        System.out.println("nNodes=" + nNodes);
        System.out.println("nEdges=" + nEdges);

        dimacsParser.writeGr(new FileOutputStream("USA-road-d.NY.dense_reduced.gr"), grReduced);
        dimacsParser.writeCo(new FileOutputStream("USA-road-d.NY.dense_reduced.co"), coReduced);
    }

    public static void main(String[] args) throws IOException {
//        createSubgraph();
        createDenseSubgraph();
    }
}
