package com.nocmok.orp.graph_index.postgres;

import com.google.common.collect.Streams;
import com.nocmok.orp.graph.api.Node;
import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class SpatialGraphMetadataStorageImpl implements SpatialGraphMetadataStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SpatialGraphMetadataStorageImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Segment mapResultSetToSegment(ResultSet rs, int nRow) throws SQLException {
        var sourceId = Objects.toString(rs.getLong("source_osm"));
        var targetId = Objects.toString(rs.getLong("target_osm"));
        return new Segment(
                sourceId + ":" + targetId,
                getNode(sourceId),
                getNode(targetId),
                rs.getDouble("cost_s")
        );
    }

    private Segment mapWideResultSetToSegment(ResultSet rs, int nRow) throws SQLException {
        var source = new Node(
                Objects.toString(rs.getLong("source_osm")),
                rs.getDouble("source_lat"),
                rs.getDouble("source_lon")
        );
        var target = new Node(
                Objects.toString(rs.getLong("target_osm")),
                rs.getDouble("target_lat"),
                rs.getDouble("target_lon")
        );
        return new Segment(source.getId() + ":" + target.getId(), source, target, rs.getDouble("cost_s"));
    }

    private Node mapResultSetToNode(ResultSet rs, int nRow) throws SQLException {
        return new Node(
                Objects.toString(rs.getLong("osm_id")),
                rs.getDouble("lat"),
                rs.getDouble("lon")
        );
    }

    @Override public Segment getSegment(String startNodeId, String endNodeId) {
        Objects.requireNonNull(startNodeId);
        Objects.requireNonNull(endNodeId);
        return getSegmentInternal(Long.parseLong(startNodeId), Long.parseLong(endNodeId));
    }

    public Segment getSegmentInternal(long startNodeId, long endNodeId) {
        var params = new HashMap<String, Object>();
        params.put("sourceId", startNodeId);
        params.put("targetId", endNodeId);
        var segments = jdbcTemplate.query(" select source_osm, target_osm, cost_s" +
                        " from ways " +
                        " where " +
                        " source_osm = :sourceId and target_osm = :targetId ",
                params,
                this::mapResultSetToSegment);
        if (segments.isEmpty()) {
            return null;
        }
        if (segments.size() != 1) {
            throw new RuntimeException("multigraphs not supported, but more than one roads exist between nodes " + startNodeId + ", " + endNodeId);
        }
        return segments.get(0);
    }

    @Override public Node getNode(String id) {
        Objects.requireNonNull(id, "node id shouldn't be null");
        return getNodeInternal(Long.parseLong(id));
    }

    public Node getNodeInternal(long id) {
        var nodes = getNodesInternal(List.of(id));
        if (nodes.isEmpty()) {
            return null;
        }
        if (nodes.size() != 1) {
            throw new RuntimeException("something wrong with table ways_vertices_pgr. more than one nodes exist with id " + id);
        }
        return nodes.get(0);
    }

    @Override public List<Segment> getSegments(List<String> startNodeIds, List<String> endNodeIds) {
        Objects.requireNonNull(startNodeIds);
        Objects.requireNonNull(endNodeIds);
        return getSegmentsInternal(
                startNodeIds.stream().map(Long::parseLong).collect(Collectors.toList()),
                endNodeIds.stream().map(Long::parseLong).collect(Collectors.toList()));
    }

    public List<Segment> getSegmentsInternal(List<Long> startNodeIds, List<Long> endNodeIds) {
        Objects.requireNonNull(startNodeIds);
        Objects.requireNonNull(endNodeIds);
        if (startNodeIds.size() != endNodeIds.size()) {
            throw new IllegalArgumentException("id list sizes don't match");
        }
        if (startNodeIds.isEmpty()) {
            return Collections.emptyList();
        }
        var concatIds = Streams.zip(startNodeIds.stream(), endNodeIds.stream(), (source, target) -> source + ":" + target)
                .collect(Collectors.toList());
        var params = new HashMap<String, Object>();
        params.put("concatIds", concatIds);
        var segments = jdbcTemplate.query(
                " select " +
                        "   t2.osm_id as source_osm, " +
                        "   t2.lat as source_lat, " +
                        "   t2.lon as source_lon, " +
                        "   t3.osm_id as target_osm, " +
                        "   t3.lat as target_lat, " +
                        "   t3.lon as target_lon, " +
                        "   t1.cost_s as cost_s " +
                        " from " +
                        " ( " +
                        "   select source_osm, target_osm, cost_s " +
                        "   from ways " +
                        "   where (source_osm::text || ':' || target_osm::text) in (:concatIds) " +
                        " ) as t1 " +
                        " join ways_vertices_pgr as t2 on t1.source_osm = t2.osm_id " +
                        " join ways_vertices_pgr as t3 on t1.target_osm = t3.osm_id ",
                params,
                this::mapWideResultSetToSegment
        );
        var segmentsMap = segments.stream()
                .collect(Collectors.toMap(Segment::getId, Function.identity()));
        var orderedSegments = new ArrayList<Segment>();
        for (var segmentId : concatIds) {
            orderedSegments.add(segmentsMap.get(segmentId));
        }
        return orderedSegments;
    }

    @Override public List<Node> getNodes(List<String> ids) {
        Objects.requireNonNull(ids);
        return getNodesInternal(ids.stream().map(Long::parseLong).collect(Collectors.toList()));
    }

    public List<Node> getNodesInternal(List<Long> ids) {
        Objects.requireNonNull(ids);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        var nodes = jdbcTemplate.query(" select osm_id, lat, lon from ways_vertices_pgr where osm_id in (:ids)", params, this::mapResultSetToNode);
        var nodesMap = nodes.stream()
                .collect(Collectors.toMap(node -> Long.parseLong(node.getId()), Function.identity()));
        var orderedNodes = new ArrayList<Node>();
        for (var id : ids) {
            orderedNodes.add(nodesMap.get(id));
        }
        return orderedNodes;
    }
}
