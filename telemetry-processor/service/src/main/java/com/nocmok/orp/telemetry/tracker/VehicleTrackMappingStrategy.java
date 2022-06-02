package com.nocmok.orp.telemetry.tracker;

import java.util.List;

/**
 * Сервис для генерирования последовательностей ребер графа по треку
 */
public interface VehicleTrackMappingStrategy {

    List<RoadSegment> matchTrackToGraph(List<LatLon> latLonTrack);

    /**
     * @param hint - ожидаемая последовательность ребер. Этим ребрам будет отдаваться предпочтение при вычислении матчинга.
     *             Если hint == null или hint - пустой список, то подсказки не учитываются
     */
    List<RoadSegment> matchTrackToGraph(List<LatLon> latLonTrack, List<RoadSegment> hint);
}
