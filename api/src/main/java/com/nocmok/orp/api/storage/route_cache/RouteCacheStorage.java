package com.nocmok.orp.api.storage.route_cache;

import java.util.List;

public interface RouteCacheStorage {

    List<RouteNode> getRouteCacheBySessionId(String sessionId);
}
