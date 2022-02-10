package com.nocmok.orp.proto;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.simulator.VehicleGPSGenerator;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.SimpleVehicle;
import com.nocmok.orp.proto.tools.DimacsGraphConverter;
import com.nocmok.orp.proto.tools.DimacsParser;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TrackGenerator {

    private static final Random random = new Random(0);

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
        return "{\\\"sessionId\\\":\\\"" + 1 + "\\\"," +
                "\\\"lat\\\":\\\"" + gps.x + "\\\"," +
                "\\\"lon\\\":\\\"" + gps.y + "\\\"," +
                "\\\"accuracy\\\":\\\"" + 10 + "\\\"," +
                "\\\"recordedAt\\\":\\\"" + Instant.now().toString() + "\\\"} \\";

    }

    private static String gpsToGcs(GPS gps) {
        return "track.add(new GCS(" + gps.x + "," + gps.y + "));";
    }

    private static GPS addNoise(GPS gps, double radius) {
        // сгенерировать угол
        // сгенерировать длину

        double noiseDirection = 2 * Math.PI * random.nextDouble();
        double noiseShift = random.nextDouble() * radius;

        double xShift = Math.cos(noiseDirection) * noiseShift;
        double yShift = Math.sin(noiseDirection) * noiseShift;

        return new GPS(gps.x + xShift, gps.y + yShift);
    }

    public static void main(String[] args) {
        var gpsGenerator = new VehicleGPSGenerator();
        var graph = loadGraph();
        List<Integer> route = List.of(31, 48, 74, 72);
        var vehicle = new SimpleVehicle(graph.getGps(31), Vehicle.State.PENDING, 10);
        vehicle.updateRoute(route);

        System.out.println(gpsToJson(graph.getGps(1)));
        GPS prevGps = null;
        for (; ; ) {
            var position = gpsGenerator.getNextVehicleGPS(graph, vehicle, 10);
            for (int j = 0; j < position.getNodesPassed(); ++j) {
                vehicle.passNode(vehicle.getRoute().get(0));
            }
            vehicle.updateGps(position.getGps());

            if (Objects.equals(prevGps, position.getGps())) {
                break;
            }
            prevGps = position.getGps();

            System.out.println(gpsToJson(position.getGps()));
        }
    }
}
