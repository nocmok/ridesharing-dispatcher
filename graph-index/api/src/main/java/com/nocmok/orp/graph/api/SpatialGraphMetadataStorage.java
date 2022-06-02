package com.nocmok.orp.graph.api;

import java.util.List;

/**
 * Интерфейс для работы с метаданными компонентов графа
 */
public interface SpatialGraphMetadataStorage {

    Segment getSegment(String startNodeId, String endNodeId);
    Node getNode(String id);

    List<Segment> getSegments(List<String> startNodeIds, List<String> endNodeIds);
    List<Node> getNodes(List<String> ids);
}
