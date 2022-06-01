package com.nocmok.orp.graph_index.postgres;

import com.nocmok.orp.graph.api.ObjectUpdater;
import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class SpatialGraphObjectsStorageImpl implements SpatialGraphObjectsStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private SpatialGraphMetadataStorageImpl metadataStorage;

    @Autowired
    public SpatialGraphObjectsStorageImpl(NamedParameterJdbcTemplate jdbcTemplate,
                                          SpatialGraphMetadataStorageImpl metadataStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.metadataStorage = metadataStorage;
    }

    private SpatialGraphObject mapResultSetToGraphObject(ResultSet rs, int nRow) throws SQLException {
        return new SpatialGraphObject(
                Objects.toString(rs.getLong("object_id")),
                metadataStorage.getSegment(Objects.toString(rs.getLong("segment_source_osm")), Objects.toString(rs.getLong("segment_target_osm"))),
                rs.getDouble("latitude"),
                rs.getDouble("longitude")
        );
    }

    @Override public Optional<SpatialGraphObject> getObject(String id) {
        Objects.requireNonNull(id, "object id shouldn't be null");
        var objects = getObjectsByIds(List.of(Long.parseLong(id)));
        if (objects.isEmpty()) {
            return Optional.empty();
        }
        if (objects.size() != 1) {
            throw new RuntimeException("something wrong with table graph_object. more than one rows with id = " + id + " exist");
        }
        return Optional.of(objects.get(0));
    }

    @Override public void updateObject(ObjectUpdater objectUpdater) {
        Objects.requireNonNull(objectUpdater);
        Objects.requireNonNull(objectUpdater.getId(), "object id shouldn't be null");
        var params = new HashMap<String, Object>();
        params.put("id", Long.parseLong(objectUpdater.getId()));
        params.put("sourceId", Optional.ofNullable(objectUpdater.getSegmentStartNodeId()).map(Long::parseLong).orElse(null));
        params.put("targetId", Optional.ofNullable(objectUpdater.getSegmentEndNodeId()).map(Long::parseLong).orElse(null));
        params.put("latitude", objectUpdater.getLatitude());
        params.put("longitude", objectUpdater.getLongitude());
        int affectedRows = jdbcTemplate.update(" insert into graph_object as t(object_id, segment_source_osm, segment_target_osm, latitude, longitude)" +
                        " values(:id, :sourceId, :targetId, :latitude, :longitude) " +
                        " on conflict(object_id) " +
                        " do update " +
                        " set " +
                        "   segment_source_osm = coalesce(:sourceId, t.segment_source_osm), " +
                        "   segment_target_osm = coalesce(:targetId, t.segment_target_osm), " +
                        "   latitude = coalesce(:latitude, t.latitude), " +
                        "   longitude = coalesce(:longitude, t.longitude) ",
                params);
        if (affectedRows != 1) {
            throw new RuntimeException("failed to update object with id " + objectUpdater.getId());
        }
    }

    @Override public void removeObject(String id) {
        Objects.requireNonNull(id, "object id shouldn't be null");
        var params = new HashMap<String, Object>();
        params.put("id", Long.parseLong(id));
        int affectedRows = jdbcTemplate.update(" delete from graph_object where object_id = :id ", params);
        if (affectedRows != 1) {
            throw new RuntimeException("failed to remove object with id " + id);
        }
    }

    private List<SpatialGraphObject> getObjectsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        var params = new HashMap<String, Object>();
        params.put("ids", ids);
        return jdbcTemplate.query(" select * from graph_object where object_id in (:ids) ",
                params, this::mapResultSetToGraphObject);
    }

    @Override public List<SpatialGraphObject> getNeighborhood(String nodeId, double radius) {
        var params = new HashMap<String, Object>();
        params.put("nodeId", Long.parseLong(nodeId));
        params.put("radius", radius);
        var objectIds = jdbcTemplate.query("select t2.object_id " +
                        " from " +
                        " ( " +
                        "   select node " +
                        "   from " +
                        "   pgr_drivingDistance('select osm_id as id, cost_s as reverse_cost, reverse_cost_s as cost, source_osm as source, target_osm as target from ways', :nodeId, :radius) " +
                        " ) as t1 " +
                        " join " +
                        " graph_object as t2 " +
                        " on t1.node = t2.segment_target_osm ",
                params,
                (rs, rn) -> rs.getLong("object_id"));
        return getObjectsByIds(objectIds);
    }
}
