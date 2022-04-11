package com.nocmok.orp.road_index.mem_stub.io;

import com.nocmok.orp.road_index.mem_stub.solver.InMemoryGraph;

import java.io.File;

public interface GraphReader {

    /**
     * @param files список файлов относящихся к одному графу
     */
    InMemoryGraph readGraph(File... files);
}
