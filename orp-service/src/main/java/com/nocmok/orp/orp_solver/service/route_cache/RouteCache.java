package com.nocmok.orp.orp_solver.service.route_cache;

import com.nocmok.orp.solver.api.RouteNode;

import java.util.List;

public interface RouteCache {

    void updateRouteCacheBySessionId(String sessionId, List<RouteNode> route);

}
