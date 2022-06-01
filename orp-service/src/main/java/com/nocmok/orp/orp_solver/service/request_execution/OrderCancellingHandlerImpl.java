package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.kafka.orp_output.RerouteNotification;
import com.nocmok.orp.orp_solver.service.notification.NotificationService;
import com.nocmok.orp.orp_solver.service.route_cache.RouteCache;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.solver.api.OrpSolver;
import com.nocmok.orp.solver.api.Request;
import com.nocmok.orp.solver.api.RoadSegment;
import com.nocmok.orp.state_keeper.api.VehicleState;
import com.nocmok.orp.state_keeper.api.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Обработчик отмены заказа, делегирующий отмену солверу
 */
@Component
public class OrderCancellingHandlerImpl implements OrderCancellingHandler {

    private OrpSolver orpSolver;
    private NotificationService notificationService;
    private RouteCache routeCache;

    @Autowired
    public OrderCancellingHandlerImpl(OrpSolver orpSolver, NotificationService notificationService,
                                      RouteCache routeCache) {
        this.orpSolver = orpSolver;
        this.notificationService = notificationService;
        this.routeCache = routeCache;
    }

    @Override public VehicleState handleOrderCancelling(VehicleState session, ServiceRequest request) {
        return orpSolver.cancelRequest(new Request(
                        request.getRequestId(),
                        new RoadSegment(request.getPickupRoadSegmentStartNodeId(), request.getPickupRoadSegmentEndNodeId()),
                        new RoadSegment(request.getDropOffRoadSegmentStartNodeId(), request.getDropOffRoadSegmentEndNodeId()),
                        request.getRecordedOriginLatitude(),
                        request.getRecordedOriginLongitude(),
                        request.getRecordedDestinationLatitude(),
                        request.getRecordedDestinationLongitude(),
                        request.getRequestedAt(),
                        request.getDetourConstraint(),
                        request.getMaxPickupDelaySeconds(),
                        request.getLoad()
                ), session.getId())
                .map(requestCancellation -> {
                    session.setSchedule(requestCancellation.getUpdatedSchedule());
                    if (requestCancellation.getUpdatedSchedule().empty()) {
                        session.setStatus(VehicleStatus.PENDING);
                    }
                    if (request.getStatus() == OrderStatus.SERVING) {
                        session.setResidualCapacity(session.getResidualCapacity() + request.getLoad());
                    }
                    routeCache.updateRouteCacheBySessionId(session.getId(), requestCancellation.getUpdatedRoute());
                    notificationService.sendNotification(RerouteNotification.builder()
                            .sessionId(session.getId())
                            .updatedRoute(requestCancellation.getUpdatedRoute())
                            .updatedSchedule(requestCancellation.getUpdatedSchedule().asList())
                            .build());
                    return session;
                })
                .orElse(session);
    }
}
