package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.service.dispatching.dto.AssignRequest;
import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.VehicleStateMapper;
import com.nocmok.orp.orp_solver.service.notification.AssignRequestNotificationService;
import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
import com.nocmok.orp.solver.api.OrpSolver;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
    private VehicleStateMapper vehicleStateMapper;

    @Autowired
    public RequestAssigningService(OrpSolver orpSolver, StateKeeper<?> stateKeeper, TransactionTemplate transactionTemplate,
                                   ServiceRequestStorageServiceImpl serviceRequestService,
                                   VehicleReservationService vehicleReservationService,
                                   AssignRequestNotificationService assignRequestNotificationService,
                                   ServiceRequestMapper serviceRequestMapper,
                                   VehicleStateMapper vehicleStateMapper) {
        this.orpSolver = orpSolver;
        this.stateKeeper = stateKeeper;
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestService = serviceRequestService;
        this.vehicleReservationService = vehicleReservationService;
        this.assignRequestNotificationService = assignRequestNotificationService;
        this.serviceRequestMapper = serviceRequestMapper;
        this.vehicleStateMapper = vehicleStateMapper;
    }

    private VehicleState getVehicleStateFromAssignRequest(AssignRequest request) {
        return stateKeeper.getActiveVehiclesByIdsForUpdate(List.of(request.getVehicleId())).stream().findFirst()
                .orElseGet(() -> {
                    log.error("received assigning request with invalid vehicle id. Request\n" + request);
                    throw new RuntimeException("invalid vehicle id");
                });
    }

    private ServiceRequestStorageService.ServiceRequestDto getServiceRequestFromAssignRequest(AssignRequest request) {
        return serviceRequestService.getRequestById(request.getServiceRequestId())
                .orElseGet(() -> {
                    log.error("received assigning request with invalid service request id. Request\n" + request);
                    throw new RuntimeException("invalid service request id");
                });

    }

    private VehicleReservation getVehicleReservationFromAssignRequest(AssignRequest request) {
        return vehicleReservationService.getReservationById(request.getReservationId())
                .orElseGet(() -> {
                    log.error("received assigning request with invalid vehicle reservation id. Request\n" + request);
                    throw new RuntimeException("invalid vehicle reservation id");
                });
    }

    public void assignRequest(AssignRequest request) {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            var vehicleState = getVehicleStateFromAssignRequest(request);
            var serviceRequest = getServiceRequestFromAssignRequest(request);
            var vehicleReservation = getVehicleReservationFromAssignRequest(request);

            if (vehicleReservation.getExpiredAt() != null) {
                transactionStatus.setRollbackOnly();
                log.info("failed to assign request " + request + "as reservation expired " + vehicleReservation);
                handleVehicleReservationExpiration(request);
                return;
            }

            var requestMatching = orpSolver.getRequestMatchingForVehicle(serviceRequestMapper.mapServiceDtoToRequest(serviceRequest), vehicleState.getId());

            if (requestMatching.isEmpty()) {
                transactionStatus.setRollbackOnly();
                log.info("cannot assign request " + request + " as vehicle unable to serve request");
                handleVehicleOutOfServiceZone(request);
                return;
            }

            vehicleState.setStatus(VehicleStatus.SERVING);
            vehicleState.setSchedule(requestMatching.get().getServingPlan().stream()
                    .map(vehicleStateMapper::mapScheduleNodeToScheduleEntry)
                    .collect(Collectors.toUnmodifiableList()));

            // Обновляем состояние тс
            stateKeeper.updateVehiclesBatch(List.of(vehicleState));

            // Отправляем нотификацию водителю
            assignRequestNotificationService.sendNotification(AssignRequestNotification.builder()
                    .serviceRequestId(request.getServiceRequestId())
                    .sessionId(request.getVehicleId())
                    .schedule(requestMatching.get().getServingPlan())
                    .routeScheduled(requestMatching.get().getServingRoute())
                    .build());

            // Снимаем резерв с тс
            vehicleReservation.setExpiredAt(Instant.now());
            vehicleReservationService.updateReservation(vehicleReservation);
        });
    }

    // TODO отправить сообщение об истечении срока резервирования
    private void handleVehicleReservationExpiration(AssignRequest request) {
//        log.info("failed to assign request as reservation expired " + request);
//        throw new UnsupportedOperationException("not implemented");
    }

    private void handleVehicleOutOfServiceZone(AssignRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }

    // TODO
    public void cancelRequest() {

    }
}
