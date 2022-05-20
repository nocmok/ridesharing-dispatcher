package com.nocmok.orp.orp_solver.config.solver;

import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.orp_solver.tools.CachingShortestRouteSolver;
import com.nocmok.orp.solver.ls.LSSolver;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LSSolverConfig {

    //    @Bean
    @Autowired
    public LSSolver lsSolver(SpatialGraphMetadataStorage graphMetadataStorage,
                             SpatialGraphObjectsStorage graphObjectsStorage,
                             ShortestRouteSolver shortestRouteSolver,
                             StateKeeper<?> stateKeeper) {
        return new LSSolver(graphMetadataStorage, graphObjectsStorage, new CachingShortestRouteSolver(shortestRouteSolver), stateKeeper);
    }
}
