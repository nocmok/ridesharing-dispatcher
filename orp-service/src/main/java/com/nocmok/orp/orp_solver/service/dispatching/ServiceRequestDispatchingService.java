package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.core_api.RequestMatching;
import com.nocmok.orp.core_api.ScheduleNode;
import com.nocmok.orp.core_api.StateKeeper;
import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.notification.dto.ServiceRequestNotification;
import com.nocmok.orp.orp_solver.service.notification.ServiceRequestNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private VehicleReservationService vehicleReservationService;
    private StateKeeper<?> stateKeeper;
    private ServiceRequestNotificationService serviceRequestNotificationService;

    @Value("${orp.orp_dispatcher.service.ServiceRequestDispatchingService.candidatesToFetch:5}")
    private Integer candidatesToFetch;

    @Autowired
    public ServiceRequestDispatchingService(OrpSolver solver,
                                            VehicleReservationService vehicleReservationService, StateKeeper<?> stateKeeper,
                                            ServiceRequestNotificationService serviceRequestNotificationService) {
        this.solver = solver;
        this.vehicleReservationService = vehicleReservationService;
        this.stateKeeper = stateKeeper;
        this.serviceRequestNotificationService = serviceRequestNotificationService;
    }

    public void dispatchServiceRequest(Request request) {

        var candidates = solver.getTopKCandidateVehicles(request, candidatesToFetch);
        if (candidates.isEmpty()) {
            log.info("no candidates to serve request\n" + request);
            // TODO сделать отправку сообщения об отказе
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

        var reservations = vehicleReservationService.tryReserveVehicles(new VehicleReservationService.ReservationCallback() {
            @Override public List<String> getVehicleIdsToCheckReservation() {
                return sortedMatchings.stream()
                        .map(RequestMatching::getServingVehicle)
                        .map(Vehicle::getId)
                        .collect(Collectors.toList());
            }

            @Override public List<VehicleReservation> reserveVehicles(List<String> feasibleVehicleIds) {
                if (feasibleVehicleIds.isEmpty()) {
                    return Collections.emptyList();
                }

                var oldChecksums = sortedMatchings.stream()
                        .map(RequestMatching::getServingVehicle)
                        .collect(Collectors.toMap(Vehicle::getId, ServiceRequestDispatchingService.this::getVehicleStateChecksum));

                var newChecksums = stateKeeper.getVehiclesByIds(feasibleVehicleIds).stream()
                        .collect(Collectors.toMap(Vehicle::getId, ServiceRequestDispatchingService.this::getVehicleStateChecksum));

                // финальный список идентификаторов тс, которым можно отправлять запрос
                var suitableVehicleIds = newChecksums.entrySet().stream()
                        .filter(entry -> oldChecksums.get(entry.getKey()).equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(HashSet::new));

                // TODO Записывать причину отказа в хранилище
                if(suitableVehicleIds.isEmpty()) {
                    log.info("Unable to satisfy request " + sortedMatchings.get(0).getRequest() + ", as state checksums mismatched for all candidate vehicles." +
                            "\nCandidate vehicles (found by algorithm): " + sortedMatchings.size() +
                            "\nReserved vehicles (vehicles that decide on another request): " + (sortedMatchings.size() - feasibleVehicleIds.size()) +
                            "\nVehicles with mismatched checksum: " + feasibleVehicleIds.size());
                }

                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> suitableVehicleIds.contains(matching.getServingVehicle().getId()))
                        .findFirst();

                return matchingToSatisfy
                        .map(ServiceRequestDispatchingService.this::mapRequestMatchingToVehicleReservation)
                        .map(List::of)
                        .orElse(Collections.emptyList());
            }

            @Override public void handleReservations(List<VehicleReservation> tickets) {
                if (tickets.isEmpty()) {
                    return;
                }

                var ticket = tickets.get(0);
                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> Objects.equals(ticket.getVehicleId(), matching.getServingVehicle().getId()))
                        .findFirst().get();

                serviceRequestNotificationService.sendNotification(new ServiceRequestNotification(
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

    private VehicleReservation mapRequestMatchingToVehicleReservation(RequestMatching matching) {
        return VehicleReservation.builder()
                .vehicleId(matching.getServingVehicle().getId())
                .requestId(matching.getRequest().getRequestId())
                .build();
    }

    private String getVehicleStateChecksum(Vehicle vehicle) {
        return Integer.toString(vehicle.getSchedule().stream()
                .map(ScheduleNode::getNodeId)
                .collect(Collectors.toList()).hashCode());
    }

    private void initiateRetry(Request request) {
        log.debug("request retry initiated, but not implemented yet ...");
    }
}
