package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.core_api.RequestMatching;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.orp_solver.service.notification.ServiceRequestNotificationDto;
import com.nocmok.orp.orp_solver.service.notification.ServiceRequestNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс-стратегия для обработки запроса на обслуживание
 */
@Slf4j
@Service
public class ServiceRequestDispatchingService {

    private OrpSolver solver;
    private TransactionTemplate transactionTemplate;
    private VehicleReservationService vehicleReservationService;
    private StateKeeper<?> stateKeeper;
    private ServiceRequestNotificationService serviceRequestNotificationService;

    @Value("${orp.orp_dispatcher.service.ServiceRequestDispatchingService.candidatesToFetch:5}")
    private Integer candidatesToFetch;

    @Autowired
    public ServiceRequestDispatchingService(OrpSolver solver, TransactionTemplate transactionTemplate,
                                            VehicleReservationService vehicleReservationService, StateKeeper<?> stateKeeper,
                                            ServiceRequestNotificationService serviceRequestNotificationService) {
        this.solver = solver;
        this.transactionTemplate = transactionTemplate;
        this.vehicleReservationService = vehicleReservationService;
        this.stateKeeper = stateKeeper;
        this.serviceRequestNotificationService = serviceRequestNotificationService;
    }

    public void dispatchServiceRequest(Request request) {
        var candidates = solver.getTopKCandidateVehicles(request, candidatesToFetch);
        if (candidates.isEmpty()) {
            log.debug("no candidates to serve request\n" + request);
            return;
        }
        log.debug(" candidate vehicles selected for request\n" + request + "\ncandidates\n" + candidates);
        var servingVehicleId = dispatchToFirstFeasibleVehicle(candidates);
        if (servingVehicleId.isEmpty()) {
            log.debug("all candidates to serve request reserved for another request\n");
            initiateRetry(request);
            return;
        }
        log.debug(" serving vehicle selected for request\n" + request + "\nserving vehicle id\n" + servingVehicleId);
    }

    private Optional<String> dispatchToFirstFeasibleVehicle(List<RequestMatching> sortedMatchings) {
        if (sortedMatchings.isEmpty()) {
            return Optional.empty();
        }

        var request = sortedMatchings.get(0).getRequest();

        var reservations = vehicleReservationService.tryReserveVehicles(new VehicleReservationService.ReservationCallback() {
            @Override public List<String> getVehicleIdsToCheckReservation() {
                var candidateVehicleIds = sortedMatchings.stream()
                        .map(matching -> matching.getServingVehicle().getId())
                        .collect(Collectors.toUnmodifiableList());

                // TODO проверять хеш сумму

                return candidateVehicleIds;
            }

            @Override public List<VehicleReservation> reserveVehicles(List<String> feasibleVehicleIds) {
                if (feasibleVehicleIds.isEmpty()) {
                    return Collections.emptyList();
                }

                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> feasibleVehicleIds.contains(matching.getServingVehicle().getId()))
                        .findFirst().get();

                return List.of(VehicleReservation.builder()
                        .vehicleId(matchingToSatisfy.getServingVehicle().getId())
                        .requestId(request.getRequestId())
                        .build());
            }

            @Override public void handleReservations(List<VehicleReservation> tickets) {
                if (tickets.isEmpty()) {
                    return;
                }

                var ticket = tickets.get(0);
                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> Objects.equals(ticket.getVehicleId(), matching.getServingVehicle().getId()))
                        .findFirst().get();

                serviceRequestNotificationService.sendNotification(new ServiceRequestNotificationDto(
                        ticket.getVehicleId(),
                        ticket.getRequestId(),
                        ticket.getReservationId(),
                        matchingToSatisfy.getServingPlan(),
                        matchingToSatisfy.getServingRoute()
                ));
            }
        });

        return reservations.stream()
                .map(VehicleReservation::getVehicleId)
                .findFirst();
    }

    private String getVehicleStateHashSum(Vehicle vehicle) {
        // TODO
        return vehicle.getSchedule().toString();
    }

    private void initiateRetry(Request request) {
        log.debug("request retry initiated, but not implemented yet ...");
    }
}
