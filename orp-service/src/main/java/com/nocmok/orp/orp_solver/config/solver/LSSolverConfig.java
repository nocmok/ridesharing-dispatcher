package com.nocmok.orp.orp_solver.config.solver;

import com.nocmok.orp.orp_solver.config.RoadIndexConfig;
import com.nocmok.orp.orp_solver.config.StateKeeperConfig;
import com.nocmok.orp.solver.ls.LSSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LSSolverConfig {

    @Autowired
    private RoadIndexConfig roadIndexConfig;
    @Autowired
    private StateKeeperConfig vehicleStateServiceConfig;

    @Bean
    public LSSolver lsSolver() {
        return new LSSolver(roadIndexConfig.dijkstraIndex(), vehicleStateServiceConfig.vehicleVehicleStateService());
    }
}
