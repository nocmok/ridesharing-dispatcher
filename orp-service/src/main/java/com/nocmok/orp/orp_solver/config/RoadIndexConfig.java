package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.road_index.mem_stub.DijkstraIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoadIndexConfig {

    @Bean
    public DijkstraIndex dijkstraIndex() {
        var classLoader = getClass().getClassLoader();
        return new DijkstraIndex(classLoader.getResourceAsStream("ny131-t.gr"), classLoader.getResourceAsStream("ny131.co"));
    }
}
