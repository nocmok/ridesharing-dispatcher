package com.nocmok.orp.graph.api;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с объектами привязанными к графу
 */
public interface SpatialGraphObjectsStorage {

    Optional<SpatialGraphObject> getObject(String id);

    /**
     * update or insert
     */
    void updateObject(ObjectUpdater objectUpdater);

    void removeObject(String id);

    /**
     * Возвращает список объектов находящихся в пределах указанного радиуса.
     * Единицы измерения радиуса совпадают с единицами измерения стоимости сегментов графа.
     */
    List<SpatialGraphObject> getNeighborhood(String nodeId, double radius);
}
