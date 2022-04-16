package com.nocmok.orp.api.service.geo;

import com.nocmok.orp.graph.api.Segment;

public interface GeolocationService {

    Segment getRoadSegmentByLatLon(double latitude, double longitude, boolean rightHandTraffic);
}
