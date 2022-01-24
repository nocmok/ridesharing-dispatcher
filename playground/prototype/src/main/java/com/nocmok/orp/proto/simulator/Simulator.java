package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Simulator {

    private ORPInstance state;
    private ORPSolver solver;
    private VehicleGPSGenerator gpsGenerator = new VehicleGPSGenerator();
    @Getter
    private Metrics metrics = new Metrics();

    public Simulator(ORPInstance state, ORPSolver solver) {
        this.state = state;
        this.solver = solver;
    }

    private void updateTotalRequests() {
        metrics.setTotalRequests(metrics.getTotalRequests() + 1);
    }

    private void updateDeniedRequests() {
        metrics.setDeniedRequests(metrics.getDeniedRequests() + 1);
    }

    private void updateTotalDistance(double extraDistance) {
        metrics.setTotalDistance(metrics.getTotalDistance() + extraDistance);
    }

    private double distance(GPS from, GPS to) {
        return Math.hypot(to.x - from.x, to.y - from.y);
    }

    public Matching acceptRequest(Request request) {
        var matching = solver.computeMatching(request);
        updateTotalRequests();

        if (matching.getDenialReason() != Matching.DenialReason.ACCEPTED) {
            updateDeniedRequests();
            request.setState(Request.State.DENIED);
            return matching;
        }

        var vehicle = matching.getServingVehicle();
        vehicle.updateRoute(matching.getRoute().getRoute());
        vehicle.updateSchedule(matching.getSchedule());
        vehicle.updateState(SimpleVehicle.State.SERVING);

        request.setState(Request.State.SERVING);

        state.getRequestLog().add(request);

        return matching;
    }

    public void ticTac(int timeSeconds) {

        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() != SimpleVehicle.State.SERVING) {
                continue;
            }
            var nextPosition = gpsGenerator.getNextVehicleGPS(state.getGraph(), vehicle, timeSeconds);

            updateTotalDistance(distance(vehicle.getGps(), nextPosition.getGps()));

            // нужно как-то получить список чекпоинтов, которые были пройдены
            var scheduleBeforeMove = new ArrayList<>(vehicle.getSchedule());

            for (int i = 0; i < nextPosition.getNodesPassed(); ++i) {
                vehicle.passNode(vehicle.getNextNode().get());
            }

            int checkpointsPassed = scheduleBeforeMove.size() - vehicle.getSchedule().size();
            for (int i = 0; i < checkpointsPassed; ++i) {
                if (scheduleBeforeMove.get(i).isArrivalCheckpoint()) {
                    scheduleBeforeMove.get(i).getRequest().setState(Request.State.SERVED);
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
