package com.nocmok.orp.orp_solver.service;

import com.nocmok.orp.core_api.RequestMatching;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.orp_solver.service.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.storage.RequestMatchingOutboxStorage;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class TaskAssigner {

    private TransactionTemplate transactionTemplate;
    private VehicleReservationService vehicleReservationService;
    private StateKeeper<?> stateKeeper;
    private RequestMatchingOutboxStorage outboxStorage;

    public TaskAssigner(TransactionTemplate transactionTemplate, VehicleReservationService vehicleReservationService,
                        StateKeeper<?> stateKeeper, RequestMatchingOutboxStorage outboxStorage) {
        this.transactionTemplate = transactionTemplate;
        this.vehicleReservationService = vehicleReservationService;
        this.stateKeeper = stateKeeper;
        this.outboxStorage = outboxStorage;
    }

    /**
     * Принимает список матчингов для одного запроса, отсортированный по приоритету.
     * Назначает запрос на первое по приоритету тс, которому есть возможность назначить задание.
     * <p>
     * Возвращает идентификатор тс которому было назначено выполнение запроса.
     * Если не получилось назначить задание ни одному тс, то возвращает Optional.empty()
     */
    public Optional<String> assign(List<RequestMatching> sortedMatchings) {
        if (sortedMatchings.isEmpty()) {
            return Optional.empty();
        }

        var candidateVehicleIds = sortedMatchings.stream()
                .map(matching -> matching.getServingVehicle().getId())
                .collect(Collectors.toUnmodifiableList());

//        var vehicleStateRequiredHashSum = sortedMatchings.stream()
//                .map(RequestMatching::getServingVehicle)
//                .collect(Collectors.toMap(Vehicle::getId, this::getVehicleStateHashSum));


        var reservations =

                vehicleReservationService.tryReserveVehicles(candidateVehicleIds, (unreservedVehicleIds) -> {
                    if (unreservedVehicleIds.isEmpty()) {
                        return Collections.emptyList();
                    }
//                    var vehicleStateHashSum = stateKeeper.getVehiclesByIds(unreservedVehicleIds).stream()
//                            .collect(Collectors.toMap(Vehicle::getId, this::getVehicleStateHashSum));

                    for (var matching : sortedMatchings) {
                        String vehicleId = matching.getServingVehicle().getId();
                        if (!unreservedVehicleIds.contains(vehicleId)) {
                            continue;
                        }
//                        if (vehicleStateRequiredHashSum.get(vehicleId).equals(vehicleStateHashSum.get(vehicleId))) {
                        outboxStorage.insert(vehicleId, matching.getRequest().getRequestId());
                        return List.of(new VehicleReservation(vehicleId, matching.getRequest().getRequestId()));
//                        }
                    }
                    return Collections.emptyList();
                });

        return reservations.isEmpty() ? Optional.empty() : Optional.of(reservations.get(0).getVehicleId());
    }

    private String getVehicleStateHashSum(Vehicle vehicle) {
        // TODO
        return vehicle.getSchedule().toString();
    }
}
