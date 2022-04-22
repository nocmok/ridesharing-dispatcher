package com.nocmok.orp.api.service.geo;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphObject;

import java.util.List;
import java.util.Optional;

public interface GeolocationService {

    Segment getRoadSegmentByLatLon(double latitude, double longitude, boolean rightHandTraffic);

    /**
     * Может вернуть неполный список, если некоторые сессии отсутствуют в хранилище
     */
    List<SpatialGraphObject> getSessionsGeodata(List<String> sessionIds);
}
