package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Route;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.ShortestPathSolver;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Getter
public class Simulator {

    private ORPInstance state;
    private ORPSolver solver;
    private VehicleGPSGenerator gpsGenerator = new VehicleGPSGenerator();
    private ShortestPathSolver shortestPathSolver;
    private Metrics metrics = new Metrics();
    private int processedRequests;

    public Simulator(ORPInstance state, ORPSolver solver) {
        this.state = state;
        this.solver = solver;
        this.shortestPathSolver = new ShortestPathSolver(state.getGraph());
    }

    private void updateTotalRequests() {
        metrics.setTotalRequests(metrics.getTotalRequests() + 1);
    }

    private void updateDeniedRequests() {
        metrics.setDeniedRequests(metrics.getDeniedRequests() + 1);
    }

    private void updateTotalDistance(double extraDistance) {
        metrics.setTotalTravelledDistance(metrics.getTotalTravelledDistance() + extraDistance);
    }

    private void updateEffectiveDistance(double extraDistance) {
        metrics.setEffectiveDistance(metrics.getEffectiveDistance() + extraDistance);
    }

    private void updateAcceptedEffectiveDistance(double extraDistance) {
        metrics.setAcceptedRequestsEffectiveDistance(metrics.getAcceptedRequestsEffectiveDistance() + extraDistance);
    }

    private void updateDeniedEffectiveDistance(double extraDistance) {
        metrics.setDeniedRequestsEffectiveDistance(metrics.getDeniedRequestsEffectiveDistance() + extraDistance);
    }

    private void updateTotalProcessingTime(long extraTime) {
        metrics.setTotalProcessingTime(metrics.getTotalProcessingTime() + extraTime);
    }

    private void updateProcessingTimeMinimum(long time) {
        if (time < metrics.getProcessingTimePerRequestMinimum()) {
            metrics.setProcessingTimePerRequestMinimum(time);
        }
    }

    private void updateProcessingTimeMaximum(long time) {
        if (time > metrics.getProcessingTimePerRequestMaximum()) {
            metrics.setProcessingTimePerRequestMaximum(time);
        }
    }

    private void updateEffectiveTravelledDistance(double extraDistance) {
        metrics.setTravelledEffectiveDistance(metrics.getTravelledEffectiveDistance() + extraDistance);
    }

    private double getRouteDistance(Route route) {
        return route.getDistance();
    }

    public Matching acceptRequest(Request request) {

        long start = System.currentTimeMillis();
        var matching = solver.computeMatching(request);
        long end = System.currentTimeMillis();
        long elapsed = end - start;

        updateTotalProcessingTime(elapsed);
        updateProcessingTimeMinimum(elapsed);
        updateProcessingTimeMaximum(elapsed);
        updateTotalRequests();

        var requestShortestRoute = shortestPathSolver.dijkstra(request.getDepartureNode(), request.getArrivalNode());
        double requestShortestRouteDistance = getRouteDistance(requestShortestRoute);
        updateEffectiveDistance(requestShortestRouteDistance);

        if (matching.getDenialReason() != Matching.DenialReason.ACCEPTED) {
            ++processedRequests;
            updateDeniedRequests();
            updateDeniedEffectiveDistance(requestShortestRouteDistance);
            request.setState(Request.State.DENIED);
            return matching;
        }

        updateAcceptedEffectiveDistance(requestShortestRouteDistance);

        var vehicle = matching.getServingVehicle();
        vehicle.updateRoute(matching.getRoute().getRoute());
        vehicle.updateSchedule(matching.getSchedule());
        vehicle.updateState(SimpleVehicle.State.SERVING);

        request.setState(Request.State.SERVING);

        state.getRequestLog().add(request);

        return matching;
    }

    private boolean vehicleTravelsWithPassenger(Vehicle vehicle) {
        int pickupCheckpoints = 0;
        int dropoffCheckpoints = 0;
        for (var checkpoint : vehicle.getSchedule()) {
            if (checkpoint.isArrivalCheckpoint()) {
                ++dropoffCheckpoints;
            } else {
                ++pickupCheckpoints;
            }
        }
        return pickupCheckpoints < dropoffCheckpoints;
    }

    public void ticTac(int timeSeconds) {

        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() != SimpleVehicle.State.SERVING) {
                continue;
            }
            var nextPosition = gpsGenerator.getNextVehicleGPS(state.getGraph(), vehicle, timeSeconds);

            // нужно как-то получить список чекпоинтов, которые были пройдены
            var scheduleBeforeMove = new ArrayList<>(vehicle.getSchedule());

            for (int i = 0; i < nextPosition.getNodesPassed(); ++i) {
                int startNode = vehicle.getNextNode().get();
                var scheduleBefore = vehicle.getSchedule();

                try {
                    vehicle.passNode(vehicle.getNextNode().get());
                }catch (NoSuchElementException e) {
                    System.out.println(vehicle);
                }

                if (scheduleBefore.size() != vehicle.getSchedule().size()) {
                    if (scheduleBefore.get(0).isArrivalCheckpoint()) {
                        if (scheduleBefore.get(0).getRequest().getLatestArrivalTime() < state.getTime()) {
                            System.out.println("[anomaly] dropoff deadline violation: actual=" + state.getTime() + ", expected=" +
                                    scheduleBefore.get(0).getRequest().getLatestArrivalTime());
                        }
                    } else {
                        if (scheduleBefore.get(0).getRequest().getLatestDepartureTime() < state.getTime()) {
                            System.out.println("[anomaly] pickup deadline violation: actual=" + state.getTime() + ", expected=" +
                                    scheduleBefore.get(0).getRequest().getLatestDepartureTime());
                        }
                    }
                }
                if (vehicle.getCurrentCapacity() < 0) {
                    System.out.println("[anomaly] vehicle capacity < 0");
                }
                if (vehicle.getNextNode().isPresent()) {
                    updateTotalDistance(state.getGraph().getRoadCost(startNode, vehicle.getNextNode().get()));
                    if (vehicleTravelsWithPassenger(vehicle)) {
                        updateEffectiveTravelledDistance(state.getGraph().getRoadCost(startNode, vehicle.getNextNode().get()));
                    }
                }
            }

            int checkpointsPassed = scheduleBeforeMove.size() - vehicle.getSchedule().size();
            for (int i = 0; i < checkpointsPassed; ++i) {
                if (scheduleBeforeMove.get(i).isArrivalCheckpoint()) {
                    scheduleBeforeMove.get(i).getRequest().setState(Request.State.SERVED);
                    ++processedRequests;
                }
            }

            vehicle.updateGps(nextPosition.getGps());

            if (vehicle.getRoute().isEmpty()) {
                vehicle.updateState(SimpleVehicle.State.PENDING);
                vehicle.getRoute().clear();
            }
        }

        state.setTime(state.getTime() + timeSeconds);
    }
}
