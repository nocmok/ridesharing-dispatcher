package com.nocmok.orp.orp_solver.config.road_index;

import com.nocmok.orp.road_index.mem_stub.DijkstraIndex;
import com.nocmok.orp.road_index.mem_stub.DimacsParser;
import com.nocmok.orp.road_index.mem_stub.InmemoryGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RoadIndexConfig {

    private final String grFileName = "ny131-t.gr";
    private final String coFileName = "ny131.co";

    @Bean
    public DimacsParser dimacsParser() {
        return new DimacsParser();
    }

    @Bean
    public InmemoryGraph graphInstance() {
        try {
            return dimacsParser().readGraph(
                    getClass().getClassLoader().getResourceAsStream(grFileName),
                    getClass().getClassLoader().getResourceAsStream(coFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DijkstraIndex dijkstraIndex() {
        return new DijkstraIndex(graphInstance());
    }
}