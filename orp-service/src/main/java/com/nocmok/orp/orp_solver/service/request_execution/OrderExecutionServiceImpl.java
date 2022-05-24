package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.solver.api.ScheduleEntryKind;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class OrderExecutionServiceImpl implements OrderExecutionService {

    private static final Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionGraph = new HashMap<>();
    private static final Set<OrderStatus> terminalOrderStatuses = new HashSet<>();

    static {
        orderStatusTransitionGraph.put(OrderStatus.SERVICE_PENDING, Set.of(OrderStatus.ACCEPTED, OrderStatus.SERVICE_DENIED));
        orderStatusTransitionGraph.put(OrderStatus.ACCEPTED, Set.of(OrderStatus.PICKUP_PENDING, OrderStatus.SERVING, OrderStatus.CANCELLED));
        orderStatusTransitionGraph.put(OrderStatus.PICKUP_PENDING, Set.of(OrderStatus.SERVING, OrderStatus.CANCELLED));
        orderStatusTransitionGraph.put(OrderStatus.SERVING, Set.of(OrderStatus.SERVED, OrderStatus.CANCELLED));
        orderStatusTransitionGraph.put(OrderStatus.SERVED, Set.of());
        orderStatusTransitionGraph.put(OrderStatus.SERVICE_DENIED, Set.of());
        orderStatusTransitionGraph.put(OrderStatus.CANCELLED, Set.of());

        terminalOrderStatuses.add(OrderStatus.SERVED);
        terminalOrderStatuses.add(OrderStatus.SERVICE_DENIED);
        terminalOrderStatuses.add(OrderStatus.CANCELLED);
    }

    private StateKeeper<?> stateKeeper;
    private OutOfOrderExecutionHandler outOfOrderExecutionHandler;
    private TransactionTemplate transactionTemplate;
    private ServiceRequestStorageServiceImpl serviceRequestStorageService;

    @Autowired
    public OrderExecutionServiceImpl(StateKeeper<?> stateKeeper,
                                     OutOfOrderExecutionHandler outOfOrderExecutionHandler,
                                     TransactionTemplate transactionTemplate,
                                     ServiceRequestStorageServiceImpl serviceRequestStorageService) {
        this.stateKeeper = stateKeeper;
        this.outOfOrderExecutionHandler = outOfOrderExecutionHandler;
        this.transactionTemplate = transactionTemplate;
        this.serviceRequestStorageService = serviceRequestStorageService;
    }

    private static boolean isTerminalOrderStatus(OrderStatus orderStatus) {
        return terminalOrderStatuses.contains(orderStatus);
    }

    /**
     * Является ли валидным обновление состояния запроса до toStatus, если текущее состояние fromStatus
     */
    private boolean validateStatusTransition(OrderStatus fromStatus, OrderStatus toStatus) {
        return orderStatusTransitionGraph.get(fromStatus).contains(toStatus);
    }

    private VehicleState handleServingStatus(VehicleState vehicleState, String orderId) {
        if (vehicleState.getSchedule().empty()) {
            throw new RuntimeException("session " + vehicleState.getId() + " has empty schedule");
        }
        var entryToRemoveOptional = vehicleState.getSchedule().asList().stream()
                .filter(scheduleEntry -> Objects.equals(orderId, scheduleEntry.getOrderId()) && ScheduleEntryKind.PICKUP == scheduleEntry.getKind())
                .findFirst();

        if (entryToRemoveOptional.isEmpty()) {
            throw new RuntimeException("no point with kind " + ScheduleEntryKind.PICKUP + " in schedule that corresponds to order with " + orderId);
        }

        var entryToRemove = entryToRemoveOptional.get();

        // Если следующая в плане точка совпадает с той, которую требуется удалить из плана,
        // то просто удаляем
        if (entryToRemove.equals(vehicleState.getSchedule().asList().get(0))) {
            vehicleState.getSchedule().removeFirstEntry();
            return vehicleState;
        }

        // Если выполнение происходит не по плану по обрабатываем отдельно
        return outOfOrderExecutionHandler.handleOutOfOrderExecution(vehicleState, entryToRemove);
    }

    private VehicleState handleServedStatus(VehicleState vehicleState, String orderId) {
        if (vehicleState.getSchedule().empty()) {
            throw new RuntimeException("session " + vehicleState.getId() + " has empty schedule");
        }
        var entryToRemoveOptional = vehicleState.getSchedule().asList().stream()
                .filter(scheduleEntry -> Objects.equals(orderId, scheduleEntry.getOrderId()) && ScheduleEntryKind.DROPOFF == scheduleEntry.getKind())
                .findFirst();
        if (entryToRemoveOptional.isEmpty()) {
            throw new RuntimeException("no point with kind " + ScheduleEntryKind.DROPOFF + " in schedule that corresponds to order with " + orderId);
        }

        var entryToRemove = entryToRemoveOptional.get();

        // Если следующая в плане точка совпадает с той, которую требуется удалить из плана,
        // то просто удаляем
        if (entryToRemove.equals(vehicleState.getSchedule().asList().get(0))) {
            vehicleState.getSchedule().removeFirstEntry();
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
                if (updatedSession.getSchedule().empty()) {
                    updatedSession.setStatus(VehicleStatus.PENDING);
                }
            } else {
                throw new RuntimeException("cannot update to status " + updatedStatus + " as its not implemented yet");
            }

            stateKeeper.updateVehicle(updatedSession);
            serviceRequestStorageService.updateRequestStatus(orderId, updatedStatus, isTerminalOrderStatus(updatedStatus));
        });

    }
}
