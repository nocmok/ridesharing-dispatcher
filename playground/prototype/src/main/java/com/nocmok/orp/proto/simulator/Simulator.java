package com.nocmok.orp.proto.simulator;

import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.ORPInstance;
import com.nocmok.orp.proto.solver.ORPSolver;
import com.nocmok.orp.proto.solver.Request;
import com.nocmok.orp.proto.solver.Vehicle;
import lombok.Getter;

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
        vehicle.getRoute().clear();
        vehicle.getRoute().addAll(matching.getRoute().getRoute());
        vehicle.setNodesPassed(0);

        vehicle.getSchedule().clear();
        vehicle.getSchedule().addAll(matching.getSchedule());
        vehicle.setCheckpointsPassed(0);

        vehicle.setState(Vehicle.State.SERVING);
        vehicle.getRequests().add(request);

        request.setState(Request.State.SERVING);
        state.getRequestLog().add(request);

        return matching;
    }

    public void ticTac(int timeSeconds) {

        for (var vehicle : state.getVehicleList()) {
            if (vehicle.getState() != Vehicle.State.SERVING) {
                continue;
            }
            var nextPosition = gpsGenerator.getNextVehicleGPS(state.getGraph(), vehicle, timeSeconds);
            int checkPointsPassed = vehicle.getCheckpointsPassed();
            for (int nodesPassed = vehicle.getNodesPassed(); nodesPassed < nextPosition.getNodesPassed(); ++nodesPassed) {
                while (checkPointsPassed < vehicle.getSchedule().size() &&
                        vehicle.getRoute().get(nodesPassed) == vehicle.getSchedule().get(checkPointsPassed).getNode()) {
                    if (vehicle.getSchedule().get(checkPointsPassed).isArrivalCheckpoint()) {

                        if (state.getTime() > vehicle.getSchedule().get(checkPointsPassed).getRequest().getLatestArrivalTime()) {
                            System.out.println("[anomaly] request served time=" + state.getTime() + ", but latest acceptable time=" +
                                    vehicle.getSchedule().get(checkPointsPassed).getRequest().getLatestArrivalTime());
                        }

                        vehicle.getSchedule().get(checkPointsPassed).getRequest().setState(Request.State.SERVED);
                    }
                    ++checkPointsPassed;
                }
            }
            vehicle.setNodesPassed(nextPosition.getNodesPassed());
            vehicle.setCheckpointsPassed(checkPointsPassed);
            vehicle.getGpsLog().add(nextPosition.getGps());

            if (vehicle.getRoute().size() == vehicle.getNodesPassed()) {
                vehicle.setState(Vehicle.State.PENDING);
                vehicle.getRoute().clear();
                vehicle.setNodesPassed(0);
            }
        }

        state.setTime(state.getTime() + timeSeconds);
    }
}
