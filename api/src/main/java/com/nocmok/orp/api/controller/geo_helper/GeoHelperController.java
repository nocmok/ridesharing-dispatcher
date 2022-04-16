package com.nocmok.orp.api.controller.geo_helper;

import com.nocmok.orp.api.controller.common_dto.Coordinates;
import com.nocmok.orp.api.controller.common_dto.Node;
import com.nocmok.orp.api.controller.common_dto.RoadSegmentWithGeodata;
import com.nocmok.orp.api.controller.geo_helper.dto.GetRoadSegmentByLatLonRequest;
import com.nocmok.orp.api.controller.geo_helper.dto.GetRoadSegmentByLatLonResponse;
import com.nocmok.orp.api.service.geo.GeolocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/geo_api/v0/location")
public class GeoHelperController {

    private final GeolocationService geolocationService;

    @Autowired
    public GeoHelperController(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    private Node mapApiNodeToDtoNode(com.nocmok.orp.graph.api.Node node) {
        return Node.builder()
                .id(node.getId())
                .coordinates(new Coordinates(node.getLatitude(), node.getLongitude()))
                .build();
    }

    @PostMapping("/road_segment")
    public @ResponseBody GetRoadSegmentByLatLonResponse getRoadSegmentByLatLon(@RequestBody GetRoadSegmentByLatLonRequest request) {
        var road = geolocationService.getRoadSegmentByLatLon(request.getCoordinates().getLatitude(), request.getCoordinates().getLongitude(),
                request.isRightHandTraffic());

        return GetRoadSegmentByLatLonResponse.builder()
                .coordinates(request.getCoordinates())
                .rightHandTraffic(request.isRightHandTraffic())
                .road(RoadSegmentWithGeodata.builder()
                        .source(mapApiNodeToDtoNode(road.getStartNode()))
                        .target(mapApiNodeToDtoNode(road.getEndNode()))
                        .build())
                .build();
    }
}
