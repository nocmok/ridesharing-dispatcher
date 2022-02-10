package com.nocmok.orp.telemetry.api;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphRoad;

import java.util.List;

/**
 * Набор операций над дорожным графом для привязки треков к графу
 */
public interface GraphToolbox {

    List<GraphRoad> getRoadNeighborhood(GCS center, double radius);
}
