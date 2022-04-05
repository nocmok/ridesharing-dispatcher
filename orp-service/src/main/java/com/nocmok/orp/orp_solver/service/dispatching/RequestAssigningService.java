package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.orp_solver.service.dispatching.dto.AssignRequest;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper;
import com.nocmok.orp.orp_solver.service.notification.AssignRequestNotificationService;
import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class RequestAssigningService {

    private OrpSolver orpSolver;
    private StateKeeper<?> stateKeeper;
    private TransactionTemplate transactionTemplate;
    private ServiceRequestStorageServiceImpl serviceRequestService;
    private VehicleReservationService vehicleReservationService;
    private AssignRequestNotificationService assignRequestNotificationService;
    private ServiceRequestMapper serviceRequestMapper;

    @Autowired
    public RequestAssigningService(OrpSolver orpSolver, StateKeeper<?> stateKeeper, TransactionTemplate transactionTemplate,
                                   ServiceRequestStorageServiceImpl serviceRequestService,
                                   VehicleReservationService vehicleReservationService,
                                   AssignRequestNotificationService assignRequestNotificationService,
                                   ServiceRequestMapper serviceRequestMapper) {
        this.orpSolver = orpSolver;
        this.stateKeeper = stateKeeper;
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestService = serviceRequestService;
        this.vehicleReservationService = vehicleReservationService;
        this.assignRequestNotificationService = assignRequestNotificationService;
        this.serviceRequestMapper = serviceRequestMapper;
    }

    public void assignRequest(AssignRequest request) {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            var vehicle = stateKeeper.getVehiclesByIds(List.of(request.getVehicleId())).stream().findFirst()
                    .orElseGet(() -> {
                        log.error("received assigning request with invalid vehicle id. Request\n" + request);
                        throw new RuntimeException("invalid vehicle id");
                    });

            var serviceRequest = serviceRequestService.getRequestById(request.getServiceRequestId())
                    .orElseGet(() -> {
                        log.error("received assigning request with invalid service request id. Request\n" + request);
                        throw new RuntimeException("invalid service request id");
                    });

            var vehicleReservation = vehicleReservationService.getReservationById(request.getReservationId())
                    .orElseGet(() -> {
                        log.error("received assigning request with invalid vehicle reservation id. Request\n" + request);
                        throw new RuntimeException("invalid vehicle reservation id");
                    });

            if (vehicleReservation.getExpiredAt() != null) {
                transactionStatus.setRollbackOnly();
                log.info("cannot assign request as vehicle reservation already expired");
                handleVehicleReservationExpiration(request);
                return;
            }

            orpSolver.acceptRequest(vehicle, serviceRequestMapper.mapServiceDtoToRequest(serviceRequest));
            // Обновляем состояние тс
            stateKeeper.updateVehiclesBatch(List.of(vehicle));

            // Отправляем нотификацию водителю
            assignRequestNotificationService.sendNotification(AssignRequestNotification.builder()
                    .serviceRequestId(request.getServiceRequestId())
                    .sessionId(request.getVehicleId())
                    .schedule(vehicle.getSchedule())
                    .routeScheduled(vehicle.getRouteScheduled())
                    .build());

            // Снимаем резерв с тс
            vehicleReservation.setExpiredAt(Instant.now());
            vehicleReservationService.updateReservation(vehicleReservation);
        });
    }

    // TODO отправить сообщение об истечении срока резервирования
    private void handleVehicleReservationExpiration(AssignRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }

    // TODO
    public void cancelRequest() {

    }
}
