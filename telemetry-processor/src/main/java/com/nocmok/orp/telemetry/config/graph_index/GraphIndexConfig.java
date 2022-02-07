package com.nocmok.orp.telemetry.config.graph_index;

import com.nocmok.orp.road_index.mem_stub.DijkstraIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphIndexConfig {

    @Bean
    public DijkstraIndex dijkstraIndex() {
        var classLoader = getClass().getClassLoader();
        return new DijkstraIndex(classLoader.getResourceAsStream("ny131-t.gr"), classLoader.getResourceAsStream("ny131.co"));
    }
}
