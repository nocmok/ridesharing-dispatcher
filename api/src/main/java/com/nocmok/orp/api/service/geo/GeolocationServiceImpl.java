package com.nocmok.orp.api.service.geo;

import com.nocmok.orp.graph.api.Segment;
import com.nocmok.orp.graph.api.SpatialGraphUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeolocationServiceImpl implements GeolocationService {

    private final SpatialGraphUtils graphUtils;

    @Autowired
    public GeolocationServiceImpl(SpatialGraphUtils graphUtils) {
        this.graphUtils = graphUtils;
    }

    @Override
    public Segment getRoadSegmentByLatLon(double latitude, double longitude, boolean rightHandTraffic) {
        return graphUtils.getClosestRoadSegment(latitude, longitude, rightHandTraffic);
    }
}
