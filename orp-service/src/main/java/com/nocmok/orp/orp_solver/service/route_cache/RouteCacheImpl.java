package com.nocmok.orp.orp_solver.service.route_cache;

import com.nocmok.orp.orp_solver.storage.route_cache.RouteCacheStorage;
import com.nocmok.orp.solver.api.RouteNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteCacheImpl implements RouteCache {

    private RouteCacheStorage routeCacheStorage;

    @Autowired
    public RouteCacheImpl(RouteCacheStorage routeCacheStorage) {
        this.routeCacheStorage = routeCacheStorage;
    }

    @Override public void updateRouteCacheBySessionId(String sessionId, List<RouteNode> route) {
        routeCacheStorage.updateRouteCacheBySessionId(sessionId, route);
    }
}
