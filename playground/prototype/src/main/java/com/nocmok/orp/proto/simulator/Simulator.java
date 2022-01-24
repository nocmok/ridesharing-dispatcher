package com.nocmok.orp.proto.simulator;

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

    public Simulator(ORPInstance state, ORPSolver solver) {
        this.state = state;
        this.solver = solver;
    }

    public Matching acceptRequest(Request request) {
        var matching = solver.computeMatching(request);

        if (matching.getDenialReason() != Matching.DenialReason.ACCEPTED) {
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
