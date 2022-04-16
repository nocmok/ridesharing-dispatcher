package com.nocmok.orp.api.config.graph;

import com.nocmok.orp.graph.api.SpatialGraphUtils;
import com.nocmok.orp.graph.mem_stub.client.GraphUtilsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphUtilsConfig {

    @Value("${graph_utils.host:localhost}")
    private String host;

    @Value("${graph_utils.port:8083}")
    private Integer port;

    @Bean
    public SpatialGraphUtils spatialGraphUtils() {
        return new GraphUtilsImpl(host, port);
    }
}
