package com.nocmok.orp.graph_index.postgres;

import com.nocmok.orp.graph.api.Route;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.ShortestRouteSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class ShortestRouteSolverImpl implements ShortestRouteSolver {

    /**
     * Средняя скорость движения (метры в секунду).
     * Используется для эвристики в алгоритме поиска кратчайшего пути A*.
     */
    @Value("${graph_index.bdAstart.averageVelocity}")
    private final Double averageVelocity = 7d;

    private NamedParameterJdbcTemplate jdbcTemplate;
    private SpatialGraphMetadataStorageImpl spatialGraphMetadataStorage;

    @Autowired
    public ShortestRouteSolverImpl(NamedParameterJdbcTemplate jdbcTemplate,
                                   SpatialGraphMetadataStorageImpl spatialGraphMetadataStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.spatialGraphMetadataStorage = spatialGraphMetadataStorage;
    }

    private Route getRouteByNodeIds(List<Long> nodeIds) {
        if (nodeIds.isEmpty()) {
            return new Route(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        var routeSegments = spatialGraphMetadataStorage.getSegmentsInternal(nodeIds.subList(0, nodeIds.size() - 1), nodeIds.subList(1, nodeIds.size()));
        var routeCost = routeSegments.stream().map(Segment::getCost).reduce(0d, Double::sum);
        return new Route(routeSegments, routeCost);
    }

    @Override public Route getShortestRoute(String originNodeId, String destinationNodeId) {
        Objects.requireNonNull(originNodeId, "origin node id shouldn't be null");
        Objects.requireNonNull(destinationNodeId, "destination node id shouldn't be null");
        if (Objects.equals(originNodeId, destinationNodeId)) {
            return new Route(Collections.emptyList(), 0d);
        }
        var params = new HashMap<String, Object>();
        params.put("sourceId", Long.parseLong(originNodeId));
        params.put("targetId", Long.parseLong(destinationNodeId));
        params.put("averageVelocity", averageVelocity);

        var nodeIds = jdbcTemplate.query(
                " select node, min(seq) as seq " +
                        " from " +
                        " pgr_aStar " +
                        " ( " +
                        "  'select osm_id as id, cost_s as cost, reverse_cost_s as reverse_cost, source_osm as source, target_osm as target, x1, y1, x2, y2 from ways', " +
                        "  :sourceId, :targetId, heuristic := 4, factor := :averageVelocity " +
                        " ) " +
                        " where path_seq = seq " +
                        " group by node " +
                        " order by seq asc ",
                params,
                (rs, rn) -> rs.getLong("node"));

        return getRouteByNodeIds(nodeIds);
    }
}
