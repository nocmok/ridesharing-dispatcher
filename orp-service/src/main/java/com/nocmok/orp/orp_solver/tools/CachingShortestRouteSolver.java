package com.nocmok.orp.orp_solver.tools;

import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.ShortestRouteSolver;

/**
 * thread-bound кеш вычисленных кратчайших маршрутов
 */
public class CachingShortestRouteSolver implements ShortestRouteSolver {

    private static final ThreadLocal<LRUCache<Pair<String>, Route>> threadLocalCache = ThreadLocal.withInitial(() -> new LRUCache<>(1000));
    private final ShortestRouteSolver underlyingSolver;

    public CachingShortestRouteSolver(ShortestRouteSolver underlyingSolver) {
        this.underlyingSolver = underlyingSolver;
    }

    @Override public Route getShortestRoute(String originNodeId, String destinationNodeId) {
        var cache = threadLocalCache.get();
        var originDestination = new Pair<>(originNodeId, destinationNodeId);
        var route = cache.get(originDestination);
        if (route == null) {
            route = underlyingSolver.getShortestRoute(originNodeId, destinationNodeId);
            cache.add(originDestination, route);
        }
        return route;
    }
}
