package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.kafka.orp_output.RequestAssignmentFailedNotification;
import com.nocmok.orp.orp_solver.service.dispatching.dto.AssignRequest;
import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper;
import com.nocmok.orp.orp_solver.service.notification.AssignRequestNotificationService;
import com.nocmok.orp.orp_solver.service.notification.dto.AssignRequestNotification;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
import com.nocmok.orp.orp_solver.service.route_cache.RouteCache;
import com.nocmok.orp.postgres.storage.OrderAssignmentStorage;
import com.nocmok.orp.postgres.storage.dto.OrderAssignment;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
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

@Service
@Slf4j
public class RequestAssigningService {

    private OrpSolver orpSolver;
    private StateKeeper<?> stateKeeper;
    private TransactionTemplate transactionTemplate;
    private ServiceRequestStorageService serviceRequestService;
    private VehicleReservationService vehicleReservationService;
    private AssignRequestNotificationService assignRequestNotificationService;
    private ServiceRequestMapper serviceRequestMapper;
    private RouteCache routeCache;
    private OrderAssignmentStorage orderAssignmentStorage;

    @Autowired
    public RequestAssigningService(OrpSolver orpSolver, StateKeeper<?> stateKeeper, TransactionTemplate transactionTemplate,
                                   ServiceRequestStorageService serviceRequestService,
                                   VehicleReservationService vehicleReservationService,
                                   AssignRequestNotificationService assignRequestNotificationService,
                                   ServiceRequestMapper serviceRequestMapper, RouteCache routeCache,
                                   OrderAssignmentStorage orderAssignmentStorage) {
        this.orpSolver = orpSolver;
        this.stateKeeper = stateKeeper;
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestService = serviceRequestService;
        this.vehicleReservationService = vehicleReservationService;
        this.assignRequestNotificationService = assignRequestNotificationService;
        this.serviceRequestMapper = serviceRequestMapper;
        this.routeCache = routeCache;
        this.orderAssignmentStorage = orderAssignmentStorage;
    }

    private VehicleState getVehicleStateFromAssignRequest(AssignRequest request) {
        return stateKeeper.getActiveVehiclesByIdsForUpdate(List.of(request.getVehicleId())).stream().findFirst()
                .orElseGet(() -> {
                    log.error("received assigning request with invalid vehicle id. Request\n" + request);
                    throw new RuntimeException("invalid vehicle id");
                });
    }

    private ServiceRequest getServiceRequestFromAssignRequest(AssignRequest request) {
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

    private void handleRequestCancelled(AssignRequest assignRequest) {
        log.info("request cancelled {} ", assignRequest.getServiceRequestId());
        assignRequestNotificationService.sendNotification(RequestAssignmentFailedNotification.builder()
                .sessionId(assignRequest.getVehicleId())
                .serviceRequestId(assignRequest.getServiceRequestId())
                .build());
    }

    public void assignRequest(AssignRequest request) {
        try {
            transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            transactionTemplate.executeWithoutResult(transactionStatus -> {

                var serviceRequest = getServiceRequestFromAssignRequest(request);
                var vehicleState = getVehicleStateFromAssignRequest(request);
                var vehicleReservation = getVehicleReservationFromAssignRequest(request);
                try {
                    if (vehicleReservation.getExpiredAt() != null) {
                        transactionStatus.setRollbackOnly();
                        log.info("failed to assign request " + request + "as reservation expired " + vehicleReservation);
                        handleVehicleReservationExpiration(request);
                        return;
                    }

                    if (serviceRequest.getStatus() != OrderStatus.SERVICE_PENDING) {
                        transactionStatus.setRollbackOnly();
                        handleRequestCancelled(request);
                        return;
                    }

                    var requestMatching =
                            orpSolver.getRequestMatchingForVehicle(serviceRequestMapper.mapServiceDtoToRequest(serviceRequest), vehicleState.getId());

                    if (requestMatching.isEmpty()) {
                        transactionStatus.setRollbackOnly();
                        log.info("cannot assign request " + request + " as vehicle unable to serve request");
                        handleVehicleOutOfServiceZone(request);
                        return;
                    }

                    routeCache.updateRouteCacheBySessionId(requestMatching.get().getServingVehicleId(), requestMatching.get().getServingRoute());
                    serviceRequestService.updateServingSessionId(serviceRequest.getRequestId(), request.getVehicleId());
                    serviceRequestService.updateRequestStatus(serviceRequest.getRequestId(), OrderStatus.ACCEPTED);

                    vehicleState.setStatus(VehicleStatus.SERVING);
                    vehicleState.setSchedule(requestMatching.get().getServingPlan());

                    // Обновляем состояние тс
                    stateKeeper.updateVehicle(vehicleState);

                    // Отправляем нотификацию водителю
                    assignRequestNotificationService.sendNotification(AssignRequestNotification.builder()
                            .serviceRequestId(request.getServiceRequestId())
                            .sessionId(request.getVehicleId())
                            .schedule(requestMatching.get().getServingPlan().asList())
                            .routeScheduled(requestMatching.get().getServingRoute())
                            .build());

                    orderAssignmentStorage.insertAssignment(OrderAssignment.builder()
                            .orderId(Long.parseLong(request.getServiceRequestId()))
                            .sessionId(Long.parseLong(request.getVehicleId()))
                            .assignedAt(Instant.now())
                            .build());
                } finally {
                    // Снимаем резерв с тс
                    vehicleReservation.setExpiredAt(Instant.now());
                    vehicleReservationService.updateReservation(vehicleReservation);
                }
            });
        } catch (Exception e) {
            // TODO записать причину отказа
            log.error(e.toString());
            serviceRequestService.updateRequestStatus(request.getServiceRequestId(), OrderStatus.SERVICE_DENIED);
        }
    }

    // TODO отправить сообщение об истечении срока резервирования
    private void handleVehicleReservationExpiration(AssignRequest request) {
//        log.info("failed to assign request as reservation expired " + request);
//        throw new UnsupportedOperationException("not implemented");
        serviceRequestService.updateRequestStatus(request.getServiceRequestId(), OrderStatus.SERVICE_DENIED);
    }

    private void handleVehicleOutOfServiceZone(AssignRequest request) {
        serviceRequestService.updateRequestStatus(request.getServiceRequestId(), OrderStatus.SERVICE_DENIED);
//        throw new UnsupportedOperationException("not implemented");
    }

    // TODO
    public void cancelRequest() {

    }
}
