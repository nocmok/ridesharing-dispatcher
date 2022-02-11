package com.nocmok.orp.orp_solver.config.service;

import com.nocmok.orp.orp_solver.config.StateKeeperConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.config.solver.LSSolverConfig;
import com.nocmok.orp.orp_solver.config.storage.StorageConfig;
import com.nocmok.orp.orp_solver.service.dispatching.ServiceRequestDispatchingService;
import com.nocmok.orp.orp_solver.service.dispatching.VehicleReservationService;
import com.nocmok.orp.orp_solver.service.notification.ServiceRequestNotificationService;
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
    private LSSolverConfig lsSolverConfig;
    @Autowired
    private StorageConfig storageConfig;

    @Bean
    public VehicleReservationService vehicleReservationService() {
        return new VehicleReservationService(postgressConfig.transactionTemplate(), storageConfig.reservationTicketSequence());
    }

    public ServiceRequestNotificationService serviceRequestNotificationService() {
        return new ServiceRequestNotificationService(storageConfig.requestMatchingOutboxStorage());
    }

    @Bean
    public ServiceRequestDispatchingService requestProcessingService() {
        return new ServiceRequestDispatchingService(lsSolverConfig.lsSolver(), postgressConfig.transactionTemplate(), vehicleReservationService(),
                stateKeeperConfig.stateKeeper(), serviceRequestNotificationService(), 10);
    }
}
