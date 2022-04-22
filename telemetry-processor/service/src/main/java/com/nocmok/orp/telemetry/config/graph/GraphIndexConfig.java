package com.nocmok.orp.telemetry.config.graph;

import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.graph.mem_stub.client.GraphObjectStorageImpl;
import com.nocmok.orp.graph.mem_stub.client.GraphUtilsImpl;
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
    public SpatialGraphObjectsStorage graphObjectsStorage() {
        return new GraphObjectStorageImpl(host, port);
    }

    @Bean
    public SpatialGraphUtils graphUtils() {
        return new GraphUtilsImpl(host, port);
    }
}
