package com.nocmok.orp.road_index.mem_stub.io;

import com.nocmok.orp.road_index.mem_stub.solver.Graph;

import java.io.File;

public interface GraphReader {

    /**
     * @param files список файлов относящихся к одному графу
     */
    Graph readGraph(File... files);

    boolean canReadFiles(File... files);
}
