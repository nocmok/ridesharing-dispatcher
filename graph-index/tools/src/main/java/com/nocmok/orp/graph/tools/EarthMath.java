package com.nocmok.orp.graph.tools;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class EarthMath {

    public static final double EARTH_RADIUS = 6_371_000;
    public static final double EARTH_CIRCUMFERENCE = 40_075_000;

    public static double haversine(double radians) {
        return (1 - cos(radians)) / 2;
    }

    public static double spheroidalDistanceDegrees(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return spheroidalDistanceRadians(toRadians(latitudeA), toRadians(longitudeA), toRadians(latitudeB), toRadians(longitudeB));
    }

    public static double spheroidalDistanceRadians(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return 2 * EARTH_RADIUS * asin(
                sqrt(pow(sin((latitudeB - latitudeA) / 2), 2)
                        + cos(latitudeA) * cos(latitudeB) * pow(sin((longitudeB - longitudeA) / 2), 2))
        );
    }
}
