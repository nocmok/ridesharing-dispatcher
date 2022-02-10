package com.nocmok.orp.proto;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.simulator.VehicleGPSGenerator;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TrackGenerator {

    private static Graph loadGraph() {
        try {
            var dimacsParser = new DimacsParser();
            var gr = dimacsParser.readGr(FXApp.class.getClassLoader().getResourceAsStream("ny131.gr"));
            var co = dimacsParser.readCo(FXApp.class.getClassLoader().getResourceAsStream("ny131.co"));
            return new DimacsGraphConverter().convert(gr, co);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String gpsToJson(GPS gps) {
        
    }

    public static void main(String[] args) {
        var gpsGenerator = new VehicleGPSGenerator();
        var graph = loadGraph();
        List<Integer> route = List.of(1, 0, 4, 14);
        var vehicle = new SimpleVehicle(graph.getGps(1), Vehicle.State.PENDING, 10);
        vehicle.updateRoute(route);

        GPS prevGps = null;
        for (;;) {
            var position = gpsGenerator.getNextVehicleGPS(graph, vehicle, 10);
            for (int j = 0; j < position.getNodesPassed(); ++j) {
                vehicle.passNode(vehicle.getRoute().get(0));
            }
            vehicle.updateGps(position.getGps());

            if(Objects.equals(prevGps, position.getGps())) {
                break;
            }
            prevGps = position.getGps();

            System.out.println(position.getGps());
        }
    }
}
