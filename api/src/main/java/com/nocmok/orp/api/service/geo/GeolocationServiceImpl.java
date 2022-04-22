package com.nocmok.orp.api.service.geo;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphObject;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GeolocationServiceImpl implements GeolocationService {

    private final SpatialGraphUtils graphUtils;
    private final SpatialGraphObjectsStorage objectsStorage;

    @Autowired
    public GeolocationServiceImpl(SpatialGraphUtils graphUtils, SpatialGraphObjectsStorage objectsStorage) {
        this.graphUtils = graphUtils;
        this.objectsStorage = objectsStorage;
    }

    @Override
    public Segment getRoadSegmentByLatLon(double latitude, double longitude, boolean rightHandTraffic) {
        return graphUtils.getClosestRoadSegment(latitude, longitude, rightHandTraffic);
    }

    @Override public List<SpatialGraphObject> getSessionsGeodata(List<String> sessionIds) {
        // TODO Добавить батч операции
        return sessionIds.stream()
                .map(objectsStorage::getObject)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
