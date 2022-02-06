package com.nocmok.orp.orp_solver.config.solver;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        LSSolverConfig.class
})
public class SolversConfig {
}
