package com.nocmok.orp.graph.api;

import java.util.List;

/**
 * Интерфейс с различными операциями над графами с геопривязкой.
 */
public interface SpatialGraphUtils {

    /**
     *
     * @param centerLatitude
     * @param centerLongitude
     * @param radius - Радиус области в метрах
     * @return
     */
    List<Segment> getRoadSegmentsWithinCircleArea(double centerLatitude, double centerLongitude, double radius);

    Segment getClosestRoadSegment(double latitude, double longitude, boolean rightHandTraffic);
}
