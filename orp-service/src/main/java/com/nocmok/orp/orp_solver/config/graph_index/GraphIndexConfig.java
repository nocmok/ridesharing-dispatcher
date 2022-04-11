package com.nocmok.orp.orp_solver.config.graph_index;

import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.graph.mem_stub.client.GraphMetadataStorageImpl;
import com.nocmok.orp.graph.mem_stub.client.GraphObjectStorageImpl;
import com.nocmok.orp.graph.mem_stub.client.ShortestRouteSolverImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphIndexConfig {

    @Value("${graph_index.server.host}")
    private String host;

    @Value("${graph_index.server.port}")
    private Integer port;

    @Bean
    public ShortestRouteSolver shortestRouteSolver() {
        return new ShortestRouteSolverImpl(host, port);
    }

    @Bean
    public SpatialGraphMetadataStorage graphMetadataStorage() {
        return new GraphMetadataStorageImpl(host, port);
    }

    @Bean
    public SpatialGraphObjectsStorage graphObjectsStorage() {
        var objectStorage = new GraphObjectStorageImpl(host, port);
        objectStorage.updateObject(new ObjectUpdater("1", "0", "1", -7.4145588E7, 4.0768E7));
        return objectStorage;
    }
}
