package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphRoad;

import java.util.List;

/**
 * Сервис для генерирования последовательностей ребер графа по треку
 */
public interface VehicleTracker {

    List<GraphRoad> matchTrackToGraph(List<GCS> track);

    /**
     * Привязывает координату к дороге из предположения, что координата принадлежит ей
     */
    GraphBinding getBinding(GraphRoad roadToBind, GCS gcs);

    /**
     * @param hint - ожидаемая последовательность ребер. Этим ребрам будет отдаваться предпочтение при вычислении матчинга.
     *             Если hint == null или hint - пустой список, то подсказки не учитываются
     */
    List<GraphRoad> matchTrackToGraph(List<GCS> track, List<GraphRoad> hint);
}
