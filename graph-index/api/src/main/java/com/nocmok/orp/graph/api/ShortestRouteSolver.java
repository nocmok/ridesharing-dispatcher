package com.nocmok.orp.graph.api;

/**
 * Интерфейс для получения кратчайших маршрутов
 */
public interface ShortestRouteSolver {

    Route getShortestRoute(String originNodeId, String destinationNodeId);
}
