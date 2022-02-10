package com.nocmok.orp.orp_solver.config.service;

import com.nocmok.orp.orp_solver.config.StateKeeperConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.config.solver.LSSolverConfig;
import com.nocmok.orp.orp_solver.config.storage.StorageConfig;
import com.nocmok.orp.orp_solver.service.RequestProcessingService;
import com.nocmok.orp.orp_solver.service.TaskAssigner;
import com.nocmok.orp.orp_solver.service.VehicleReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Autowired
    private PostgressConfig postgressConfig;
    @Autowired
    private StateKeeperConfig stateKeeperConfig;
    @Autowired
    private StorageConfig storageConfig;
    @Autowired
    private LSSolverConfig lsSolverConfig;

    @Bean
    public VehicleReservationService vehicleReservationService() {
        return new VehicleReservationService(postgressConfig.transactionTemplate(), postgressConfig.namedParameterJdbcTemplate());
    }

    @Bean
    public TaskAssigner taskAssigner() {
        return new TaskAssigner(postgressConfig.transactionTemplate(), vehicleReservationService(), stateKeeperConfig.vehicleVehicleStateService(),
                storageConfig.requestMatchingOutboxStorage());
    }

    @Bean
    public RequestProcessingService requestProcessingService() {
        return new RequestProcessingService(lsSolverConfig.lsSolver(), taskAssigner(), 10);
    }
}
