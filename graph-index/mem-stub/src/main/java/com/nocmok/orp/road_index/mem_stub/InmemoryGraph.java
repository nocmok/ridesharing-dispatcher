package com.nocmok.orp.road_index.mem_stub;

import java.util.List;

public interface InmemoryGraph {

    List<List<Edge>> adjacencyList();

    List<double[]> coordinates();
}
