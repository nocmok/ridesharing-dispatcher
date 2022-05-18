package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.orp_solver.service.dispatching.dto.VehicleReservation;
import com.nocmok.orp.orp_solver.service.dispatching.mapper.ServiceRequestMapper;
import com.nocmok.orp.orp_solver.service.notification.ServiceRequestNotificationService;
import com.nocmok.orp.orp_solver.service.notification.dto.ServiceRequestNotification;
import com.nocmok.orp.orp_solver.service.request_management.ServiceRequestStorageServiceImpl;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.solver.api.OrpSolver;
import com.nocmok.orp.solver.api.RequestMatching;
import com.nocmok.orp.solver.api.Schedule;
import com.nocmok.orp.state_keeper.api.StateKeeper;
import com.nocmok.orp.state_keeper.api.VehicleState;
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
public class ServiceRequestDispatchingServiceImpl implements ServiceRequestDispatchingService {

    private OrpSolver solver;
    private VehicleReservationService vehicleReservationService;
    private StateKeeper<?> stateKeeper;
    private ServiceRequestNotificationService serviceRequestNotificationService;
    private ServiceRequestMapper serviceRequestMapper;
    private ServiceRequestStorageServiceImpl serviceRequestStorageService;

    @Value("${orp.orp_dispatcher.service.ServiceRequestDispatchingService.candidatesToFetch:5}")
    private Integer candidatesToFetch;

    @Autowired
    public ServiceRequestDispatchingServiceImpl(OrpSolver solver, VehicleReservationService vehicleReservationService,
                                                StateKeeper<?> stateKeeper,
                                                ServiceRequestNotificationService serviceRequestNotificationService,
                                                ServiceRequestMapper serviceRequestMapper,
                                                ServiceRequestStorageServiceImpl serviceRequestStorageService) {
        this.solver = solver;
        this.vehicleReservationService = vehicleReservationService;
        this.stateKeeper = stateKeeper;
        this.serviceRequestNotificationService = serviceRequestNotificationService;
        this.serviceRequestMapper = serviceRequestMapper;
        this.serviceRequestStorageService = serviceRequestStorageService;
    }

    @Override
    public void dispatchServiceRequest(ServiceRequest serviceRequest) {
        var candidates =
                solver.getTopKCandidateVehicles(serviceRequestMapper.mapServiceDtoToRequest(serviceRequest), candidatesToFetch);
        if (candidates.isEmpty()) {
            log.info("no candidates to serve request\n" + serviceRequest);
            serviceRequestStorageService.updateRequestStatus(serviceRequest.getRequestId(), OrderStatus.SERVICE_DENIED);
            // TODO сделать отправку сообщения об отказе
            return;
        }
        log.debug(" candidate vehicles selected for request\n" + serviceRequest + "\ncandidates\n" + candidates);
        var servingVehicleId = dispatchToFirstFeasibleVehicle(serviceRequest, candidates);
        if (servingVehicleId.isEmpty()) {
            log.debug("all candidates to serve request reserved for another request\n");
            initiateRetry(serviceRequest);
            return;
        }
        log.debug(" serving vehicle selected for request\n" + serviceRequest + "\nserving vehicle id\n" + servingVehicleId);
    }

    private Optional<String> dispatchToFirstFeasibleVehicle(ServiceRequest request, List<RequestMatching> sortedMatchings) {
        if (sortedMatchings.isEmpty()) {
            return Optional.empty();
        }

        var reservations = vehicleReservationService.tryReserveVehicles(new VehicleReservationService.ReservationCallback() {
            @Override public List<String> getVehicleIdsToCheckReservation() {
                return sortedMatchings.stream()
                        .map(RequestMatching::getServingVehicleId)
                        .collect(Collectors.toList());
            }

            @Override public List<VehicleReservation> reserveVehicles(List<String> feasibleVehicleIds) {
                if (feasibleVehicleIds.isEmpty()) {
                    return Collections.emptyList();
                }

                // Планы тс которые ожидаются.
                var expectedSchedules = sortedMatchings.stream()
                        .collect(Collectors.toMap(RequestMatching::getServingVehicleId, RequestMatching::getOldServingPlan));

                // Фактические планы тс на текущий момент
                var actualSchedules = stateKeeper.getVehiclesByIds(feasibleVehicleIds).stream()
                        .collect(Collectors.toMap(VehicleState::getId, VehicleState::getSchedule));

                // финальный список идентификаторов тс, которым можно отправлять запрос
                var suitableVehicleIds = actualSchedules.entrySet().stream()
                        .filter(vehicleIdAndSchedule -> schedulesEquals(vehicleIdAndSchedule.getValue(), expectedSchedules.get(vehicleIdAndSchedule.getKey())))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(HashSet::new));

                // TODO Записывать причину отказа в хранилище
                if (suitableVehicleIds.isEmpty()) {
                    log.info(
                            "Unable to satisfy request " + request + " as state checksums mismatched for all candidate vehicles." +
                                    "\nCandidate vehicles (found by algorithm): " + sortedMatchings.size() +
                                    "\nReserved vehicles (vehicles that decide on another request): " + (sortedMatchings.size() - feasibleVehicleIds.size()) +
                                    "\nVehicles with mismatched checksum: " + feasibleVehicleIds.size());
                }

                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> suitableVehicleIds.contains(matching.getServingVehicleId()))
                        .findFirst();

                return matchingToSatisfy
                        .map(matching -> VehicleReservation.builder()
                                .vehicleId(matching.getServingVehicleId())
                                .requestId(request.getRequestId())
                                .build()
                        )
                        .map(List::of)
                        .orElse(Collections.emptyList());
            }

            @Override public void handleReservations(List<VehicleReservation> reservations) {
                if (reservations.isEmpty()) {
                    serviceRequestStorageService.updateRequestStatus(request.getRequestId(), OrderStatus.SERVICE_DENIED);
                    return;
                }

                var reservation = reservations.get(0);
                var matchingToSatisfy = sortedMatchings.stream()
                        .filter(matching -> Objects.equals(reservation.getVehicleId(), matching.getServingVehicleId()))
                        .findFirst().get();

                serviceRequestNotificationService.sendNotification(new ServiceRequestNotification(
                        reservation.getVehicleId(),
                        reservation.getRequestId(),
                        reservation.getReservationId(),
                        matchingToSatisfy.getServingPlan().asList(),
                        matchingToSatisfy.getServingRoute()
                ));
            }
        });

        return reservations.stream()
                .map(VehicleReservation::getVehicleId)
                .findFirst();
    }

    private boolean schedulesEquals(Schedule scheduleOne, Schedule scheduleTwo) {
        return Objects.equals(scheduleOne.asList(), scheduleTwo.asList());
    }

    private void initiateRetry(ServiceRequest serviceRequestServiceDto) {
        serviceRequestStorageService.updateRequestStatus(serviceRequestServiceDto.getRequestId(), OrderStatus.SERVICE_DENIED);
        log.debug("request retry initiated, but not implemented yet ...");
    }
}
