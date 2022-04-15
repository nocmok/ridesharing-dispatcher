package com.nocmok.orp.api.config.graph;

import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.mem_stub.client.GraphObjectStorageImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphObjectsStorageConfig {

    @Value("${graph_object_storage.host:localhost}")
    private String host;

    @Value("${graph_object_storage.port:8083}")
    private Integer port;

    @Bean
    public SpatialGraphObjectsStorage graphObjectsStorage() {
        return new GraphObjectStorageImpl(host, port);
    }
}
