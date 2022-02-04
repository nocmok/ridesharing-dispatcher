package com.nocmok.orp.core_api;

import java.util.List;

// 1) Добавление точечных объектов в индекс
// 2) Удаление объектов из индекса
// 3) Получение метаданных компонентов графа
// 4) Поиск кратчайших путей между нодами графа
// 5) Поиск соседей в заданном радиусе (по кратчайшим маршрутам)
public interface RoadIndex {
    RoadRoute shortestRoute(RoadNode a, RoadNode b);
    List<Vehicle> getNeighbors(GCS center, double radius);
}
