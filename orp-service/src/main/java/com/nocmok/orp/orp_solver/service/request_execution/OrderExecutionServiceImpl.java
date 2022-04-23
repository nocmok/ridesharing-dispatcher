package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageService;
import com.nocmok.orp.state_keeper.api.ScheduleEntryKind;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class OrderExecutionServiceImpl implements OrderExecutionService {

    private static final Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionGraph = new HashMap<>();

    static {
        orderStatusTransitionGraph.put(OrderStatus.PENDING, Set.of(OrderStatus.SERVING, OrderStatus.DENIED));
        orderStatusTransitionGraph.put(OrderStatus.SERVING, Set.of(OrderStatus.SERVED, OrderStatus.SERVING_DENIED));
        orderStatusTransitionGraph.put(OrderStatus.SERVED, Set.of());
        orderStatusTransitionGraph.put(OrderStatus.DENIED, Set.of());
        orderStatusTransitionGraph.put(OrderStatus.SERVING_DENIED, Set.of());
    }

    private StateKeeper<?> stateKeeper;
    private OutOfOrderExecutionHandler outOfOrderExecutionHandler;
    private TransactionTemplate transactionTemplate;
    private ServiceRequestStorageService serviceRequestStorageService;

    @Autowired
    public OrderExecutionServiceImpl(StateKeeper<?> stateKeeper,
                                     OutOfOrderExecutionHandler outOfOrderExecutionHandler,
                                     TransactionTemplate transactionTemplate,
                                     ServiceRequestStorageService serviceRequestStorageService) {
        this.stateKeeper = stateKeeper;
        this.outOfOrderExecutionHandler = outOfOrderExecutionHandler;
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestStorageService = serviceRequestStorageService;
    }

    /**
     * Является ли валидным обновление состояния запроса до toStatus, если текущее состояние fromStatus
     */
    private boolean validateStatusTransition(OrderStatus fromStatus, OrderStatus toStatus) {
        return orderStatusTransitionGraph.get(fromStatus).contains(toStatus);
    }

    private VehicleState handleServingStatus(VehicleState vehicleState, String orderId) {
        if (CollectionUtils.isEmpty(vehicleState.getSchedule())) {
            throw new RuntimeException("session " + vehicleState.getId() + " has empty schedule");
        }
        var entryToRemoveOptional = vehicleState.getSchedule().stream()
                .filter(scheduleEntry -> Objects.equals(orderId, scheduleEntry.getOrderId()) && ScheduleEntryKind.PICKUP == scheduleEntry.getKind())
                .findFirst();
        if (entryToRemoveOptional.isEmpty()) {
            throw new RuntimeException("no point with kind " + ScheduleEntryKind.PICKUP + " in schedule that corresponds to order with " + orderId);
        }

        var entryToRemove = entryToRemoveOptional.get();

        // Если следующая в плане точка совпадает с той, которую требуется удалить из плана,
        // то просто удаляем
        if (entryToRemove.equals(vehicleState.getSchedule().get(0))) {
            vehicleState.getSchedule().remove(0);
            return vehicleState;
        }

        // Если выполнение происходит не по плану по обрабатываем отдельно
        return outOfOrderExecutionHandler.handleOutOfOrderExecution(vehicleState, entryToRemove);
    }

    private VehicleState handleServedStatus(VehicleState vehicleState, String orderId) {
        if (CollectionUtils.isEmpty(vehicleState.getSchedule())) {
            throw new RuntimeException("session " + vehicleState.getId() + " has empty schedule");
        }
        var entryToRemoveOptional = vehicleState.getSchedule().stream()
                .filter(scheduleEntry -> Objects.equals(orderId, scheduleEntry.getOrderId()) && ScheduleEntryKind.DROPOFF == scheduleEntry.getKind())
                .findFirst();
        if (entryToRemoveOptional.isEmpty()) {
            throw new RuntimeException("no point with kind " + ScheduleEntryKind.DROPOFF + " in schedule that corresponds to order with " + orderId);
        }

        var entryToRemove = entryToRemoveOptional.get();

        // Если следующая в плане точка совпадает с той, которую требуется удалить из плана,
        // то просто удаляем
        if (entryToRemove.equals(vehicleState.getSchedule().get(0))) {
            vehicleState.getSchedule().remove(0);
            return vehicleState;
        }

        // Если выполнение происходит не по плану по обрабатываем отдельно
        return outOfOrderExecutionHandler.handleOutOfOrderExecution(vehicleState, entryToRemove);
    }

    @Override public void updateOrderStatus(String sessionId, String orderId, OrderStatus updatedStatus) {
        transactionTemplate.executeWithoutResult(status -> {

            var requestDetailsOptional = serviceRequestStorageService.getRequestByIdForUpdate(orderId);
            if (requestDetailsOptional.isEmpty()) {
                throw new RuntimeException("request with id " + orderId + " not present in request storage");
            }
            var requestDetails = requestDetailsOptional.get();

            if (!validateStatusTransition(requestDetails.getStatus(), updatedStatus)) {
                throw new IllegalRequestExecution("cannot update request from status " + requestDetails.getStatus() + " to status " + updatedStatus);
            }

            var sessions = stateKeeper.getActiveVehiclesByIdsForUpdate(List.of(sessionId));
            if (sessions.isEmpty()) {
                throw new RuntimeException("no session found with id " + sessionId);
            }
            if (sessions.size() > 1) {
                log.warn("found duplicate sessions " + sessions);
            }
            var session = sessions.get(0);

            VehicleState updatedSession;

            if (OrderStatus.SERVING.equals(updatedStatus)) {
                if (session.getResidualCapacity() < requestDetails.getLoad()) {
                    log.warn("Anomaly state detected (negative vehicle capacity)");
                }
                updatedSession = handleServingStatus(session, orderId);
                updatedSession.setResidualCapacity(session.getResidualCapacity() - requestDetails.getLoad());
            } else if (OrderStatus.SERVED.equals(updatedStatus)) {
                updatedSession = handleServedStatus(session, orderId);
                updatedSession.setResidualCapacity(session.getResidualCapacity() + requestDetails.getLoad());
                if (CollectionUtils.isEmpty(updatedSession.getSchedule())) {
                    updatedSession.setStatus(VehicleStatus.PENDING);
                }
            } else {
                throw new RuntimeException("cannot update to status " + updatedStatus + " as its not implemented yet");
            }

            stateKeeper.updateVehiclesBatch(List.of(updatedSession));
            serviceRequestStorageService.updateRequestStatus(orderId, updatedStatus);
        });

    }
}
