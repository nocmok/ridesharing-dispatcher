package com.nocmok.orp.orp_solver.config.solver;

import com.nocmok.orp.graph.api.ShortestRouteSolver;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.orp_solver.tools.CachingShortestRouteSolver;
import com.nocmok.orp.solver.kt.KTSolver;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KTSolverConfig {

    @Value("${kt_solver.max_allowed_kinetic_tree_size:32}")
    private Integer maxAllowedKineticTreeSize;

    @Value("${kt_solver.max_candidates_to_check:10}")
    private Integer maxCandidatesToCheck;

    @Bean
    @Autowired
    public KTSolver ktSolver(SpatialGraphMetadataStorage graphMetadataStorage,
                             SpatialGraphObjectsStorage graphObjectsStorage,
                             ShortestRouteSolver shortestRouteSolver,
                             StateKeeper<?> stateKeeper) {
        return new KTSolver(graphMetadataStorage, graphObjectsStorage, new CachingShortestRouteSolver(shortestRouteSolver), stateKeeper,
                maxAllowedKineticTreeSize, maxCandidatesToCheck);
    }
}
