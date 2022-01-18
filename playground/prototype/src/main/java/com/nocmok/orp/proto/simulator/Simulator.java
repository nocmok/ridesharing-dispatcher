package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Vehicle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Simulator {

    private ORPInstance state;
    private ORPSolver solver;
    private VehicleGPSGenerator gpsGenerator = new VehicleGPSGenerator();
    private List<Request> requestsLog = new ArrayList<>();

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
            return;
        }
        matching.getServingVehicle().setSchedule(matching.getAugmentedRoute());
        matching.getServingVehicle().setNodesPassed(1);
        matching.getServingVehicle().setState(Vehicle.State.SERVING);
        requestsLog.add(request);
    }

    public void ticTac(int timeSeconds) {
        gpsGenerator.moveVehicles(state.getGraph(), state.getVehicleList(), timeSeconds);
        state.setTime(state.getTime() + timeSeconds);
        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() == Vehicle.State.SERVING && vehicle.getNodesPassed() >= vehicle.getSchedule().size()) {
                vehicle.setState(Vehicle.State.PENDING);
                vehicle.setSchedule(Collections.emptyList());
                vehicle.setNodesPassed(1);
            }
        }
    }
}
