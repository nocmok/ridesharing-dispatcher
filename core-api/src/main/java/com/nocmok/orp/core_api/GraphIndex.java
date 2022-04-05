package com.nocmok.orp.core_api;

import java.util.List;


/**
 * Фасад дорожного индекса для использования реализациями интерфейса OrpSolver
 */
public interface GraphIndex {

    /**
     * Для вычисления кратчайшего маршрута в графе
     */
    GraphRoute shortestRoute(int startNodeId, int endNodeId);

    /**
     * Для получения всех объектов в индексе в окрестности точки.
     * <p>
     * Окрестность определяется топологией графа, а не кратчайшим расстоянием от объекта до точки.
     * В окрестность входят такие объекты, что от объекта до заданной точки существует маршрут
     * стоимостью не превышающей указанную
     */
    List<GraphIndexEntity> getNeighborhood(GCS center, double radius);

    /**
     * Для вычисления стоимости маршрута
     */
    double getRouteCost(List<GraphNode> route);

    GCS getNodeCoordinates(int nodeId);
}
