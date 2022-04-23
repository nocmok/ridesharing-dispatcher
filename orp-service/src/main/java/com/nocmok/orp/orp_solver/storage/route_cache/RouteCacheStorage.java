package com.nocmok.orp.orp_solver.storage.route_cache;

import com.nocmok.orp.solver.api.RouteNode;

import java.util.List;

public interface RouteCacheStorage {

    void updateRouteCacheBySessionId(String sessionId, List<RouteNode> route);
}
