package com.nocmok.orp.solver.kt;

import com.nocmok.orp.graph.tools.EarthMath;

public class DumbRoadProgressStrategy implements RoadProgressStrategy {

    @Override public Double getProgress(Double roadStartNodeLatitude, Double roadStartNodeLongitude, Double roadEndNodeLatitude, Double roadEndNodeLongitude,
                                        Double vehicleLatitude, Double vehicleLongitude) {
        double distancePassed = EarthMath.spheroidalDistanceDegrees(
                roadStartNodeLatitude,
                roadStartNodeLongitude,
                vehicleLatitude,
                vehicleLongitude
        );
        double distanceToPass = EarthMath.spheroidalDistanceDegrees(
                vehicleLatitude,
                vehicleLongitude,
                roadEndNodeLatitude,
                roadEndNodeLongitude
        );
        return distancePassed / (distancePassed + distanceToPass);
    }
}
