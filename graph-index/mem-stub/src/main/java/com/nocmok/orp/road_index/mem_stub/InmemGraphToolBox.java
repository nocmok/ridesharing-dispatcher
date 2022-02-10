package com.nocmok.orp.road_index.mem_stub;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphRoad;
import com.nocmok.orp.telemetry.api.GraphToolbox;

import java.util.ArrayList;
import java.util.List;

public class InmemGraphToolBox implements GraphToolbox {

    private final Math2d math2d = new Math2d();

    private final InmemoryGraphOrpWrapper graph;

    public InmemGraphToolBox(InmemoryGraph graph) {
        this.graph = new InmemoryGraphOrpWrapper(graph);
    }

    private boolean checkBounds(double val, double bound1, double bound2) {
        return val >= Double.min(bound1, bound2) && val <= Double.max(bound1, bound2);
    }

    /**
     * Считает список ребер в пределах заданной окрестности
     */
    @Override public List<GraphRoad> getRoadNeighborhood(GCS center, double radius) {
        var neighborsRoads = new ArrayList<GraphRoad>();
        for (var road : graph.getRoads()) {
            GCS projection = math2d.projection2d(center, road.getStartNode().getCoordinates(), road.getEndNode().getCoordinates());
            if (math2d.distance(center, projection) > radius) {
                continue;
            }

            // Если проекция внутри отрезка, то добавляем ребро в соседей
            if (checkBounds(projection.lat(), road.getStartNode().getCoordinates().lat(), road.getEndNode().getCoordinates().lat()) &&
                    checkBounds(projection.lon(), road.getStartNode().getCoordinates().lon(), road.getEndNode().getCoordinates().lon())) {
                neighborsRoads.add(road);
                continue;
            }

            // иначе если хотя бы один конец отрезка внутри области, то добавляем ребро в соседей
            if (math2d.distance(center, road.getStartNode().getCoordinates()) <= radius ||
                    math2d.distance(center, road.getEndNode().getCoordinates()) <= radius) {
                neighborsRoads.add(road);
            }
        }

        return neighborsRoads;
    }

}
