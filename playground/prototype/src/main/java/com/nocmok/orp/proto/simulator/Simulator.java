package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Vehicle;
import lombok.Getter;

import java.util.Collections;

@Getter
public class Simulator {

    private ORPInstance state;
    private ORPSolver solver;
    private VehicleGPSGenerator gpsGenerator = new VehicleGPSGenerator();

    public Simulator(ORPInstance state, ORPSolver solver) {
        this.state = state;
        this.solver = solver;
    }

    public void addVehicle(Vehicle vehicle) {
        state.getVehicleList().add(vehicle);
    }

    public void acceptRequest(Request request) {
        var matching = solver.computeMatching(request);
        if (matching.getDenialReason() != Matching.DenialReason.ACCEPTED) {
            request.setState(Request.State.DENIED);
            return;
        }
        matching.getServingVehicle().setSchedule(matching.getAugmentedRoute());
        matching.getServingVehicle().setNodesPassed(1);
        matching.getServingVehicle().setState(Vehicle.State.SERVING);
        matching.getServingVehicle().getRequests().add(request);
        request.setState(Request.State.SERVING);
        state.getRequestLog().add(request);
    }

    public void ticTac(int timeSeconds) {
        gpsGenerator.moveVehicles(state.getGraph(), state.getVehicleList(), timeSeconds);
        state.setTime(state.getTime() + timeSeconds);
        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() == Vehicle.State.SERVING &&
                    vehicle.getNodesPassed() >= vehicle.getSchedule().size()) {
                vehicle.setState(Vehicle.State.PENDING);
                vehicle.setSchedule(Collections.emptyList());
                vehicle.setNodesPassed(1);

                // TODO Не будет работать на нормальных алгоритмах. Переделать!!!
                vehicle.getRequests().get(vehicle.getRequests().size() - 1).setState(Request.State.SERVED);
            }
        }
    }
}
