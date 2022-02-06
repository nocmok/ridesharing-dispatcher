package com.nocmok.orp.core_api;

import java.util.List;


/**
 * Фасад дорожного индекса для использования реализациями интерфейса OrpSolver
 */
public interface RoadIndex {

    /**
     * Для вычисления кратчайшего маршрута в графе
     */
    RoadRoute shortestRoute(int startNodeId, int endNodeId);

    /**
     * Для получения всех объектов в индексе в окрестности точки.
     * <p>
     * Окрестность определяется топологией графа, а не кратчайшим расстоянием от объекта до точки.
     * В окрестность входят такие объекты, что от объекта до заданной точки существует маршрут
     * стоимостью не превышающей указанную
     */
    List<RoadIndexEntity> getNeighborhood(GCS center, double radius);
}
