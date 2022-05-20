package com.nocmok.orp.graph_index.postgres;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SpatialGraphUtilsImpl implements SpatialGraphUtils {

    private static final Integer WGS84_SRID = 4326;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private SpatialGraphMetadataStorageImpl metadataStorage;

    @Autowired
    public SpatialGraphUtilsImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 SpatialGraphMetadataStorageImpl spatialGraphMetadataStorage) {
        this.jdbcTemplate = namedParameterJdbcTemplate;
        this.metadataStorage = spatialGraphMetadataStorage;
    }

    private String getPointGeometryStringByLatLon(double lat, double lon) {
        return "SRID=" + WGS84_SRID + ";POINT(" + lon + " " + lat + ")";
    }

    @Override public List<Segment> getRoadSegmentsWithinCircleArea(double centerLatitude, double centerLongitude, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius should not be negative");
        }
        var params = new HashMap<String, Object>();
        params.put("center", getPointGeometryStringByLatLon(centerLatitude, centerLongitude));
        var segmentIds = jdbcTemplate.query(" select source_osm, target_osm, (one_way = 2) as reversible " +
                                " from ways " +
                                " where ST_DWithin(:center::geography, the_geom::geography, " + radius + ") " +
                                " order by :center::geometry <-> the_geom::geometry; ",
                        params,
                        (rs, rn) -> {
                            var sourceId = rs.getLong("source_osm");
                            var targetId = rs.getLong("target_osm");
                            if (rs.getBoolean("reversible")) {
                                return Stream.of(new long[]{sourceId, targetId}, new long[]{targetId, sourceId});
                            }
                            return Stream.of(new long[]{sourceId, targetId});
                        }).stream()
                .flatMap(Function.identity())
                .collect(Collectors.toList());
        var sourceIds = segmentIds.stream().map(s -> s[0]).collect(Collectors.toList());
        var targetIds = segmentIds.stream().map(s -> s[1]).collect(Collectors.toList());
        return metadataStorage.getSegmentsInternal(sourceIds, targetIds);
    }

    private boolean isLatLonInRightSemiPlane(double lat, double lon, double sourceLat, double sourceLon, double targetLat, double targetLon) {
        double ax = targetLon - sourceLon;
        double ay = targetLat - sourceLat;

        double bx = lon - sourceLon;
        double by = lat - targetLat;

        return (ax * by - ay * bx) <= 0;
    }

    @Override public Segment getClosestRoadSegment(double latitude, double longitude, boolean rightHandTraffic) {
        var params = new HashMap<String, Object>();
        params.put("point", getPointGeometryStringByLatLon(latitude, longitude));
        var rowSet = jdbcTemplate.queryForRowSet(
                " select source_osm, target_osm, x1 as source_lon, y1 as source_lat, x2 as target_lon, y2 as target_lat, (one_way = 2) as reversible " +
                        " from ways " +
                        " order by the_geom::geometry <-> :point::geometry " +
                        " limit 1 ",
                params
        );
        if (!rowSet.next()) {
            return null;
        }
        var reversible = rowSet.getBoolean("reversible");
        if (!reversible) {
            return metadataStorage.getSegmentInternal(rowSet.getLong("source_osm"), rowSet.getLong("target_osm"));
        }
        if (rightHandTraffic == isLatLonInRightSemiPlane(latitude, longitude, rowSet.getDouble("source_lat"), rowSet.getDouble("source_lon"),
                rowSet.getDouble("target_lat"), rowSet.getDouble("target_lon"))) {
            return metadataStorage.getSegmentInternal(rowSet.getLong("source_osm"), rowSet.getLong("target_osm"));
        } else {
            return metadataStorage.getSegmentInternal(rowSet.getLong("target_osm"), rowSet.getLong("source_osm"));
        }
    }
}
