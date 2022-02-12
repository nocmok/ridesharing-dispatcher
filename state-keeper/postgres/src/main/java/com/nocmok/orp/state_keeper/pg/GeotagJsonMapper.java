package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphBinding;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;

class GeotagJsonMapper {

    private ObjectMapper objectMapper;

    public GeotagJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private NodeJsonDto mapGraphNodeToJsonDto(GraphNode graphNode) {
        return new NodeJsonDto(
                graphNode.getNodeId(),
                new CoordinatesJsonDto(graphNode.getCoordinates().lat(), graphNode.getCoordinates().lon())
        );
    }

    private GraphNode mapJsonDtoToGraphNode(NodeJsonDto dto) {
        return new GraphNode(
                dto.getId(),
                new GCS(dto.getCoordinates().getLatitude(), dto.getCoordinates().getLongitude())
        );
    }

    private GeotagJsonDto mapGeotagToJsonDto(Geotag geotag) {
        return new GeotagJsonDto(
                new CoordinatesJsonDto(geotag.getGcs().lat(), geotag.getGcs().lon()),
                new RoadJsonDto(
                        mapGraphNodeToJsonDto(geotag.getGraphBinding().getRoad().getStartNode()),
                        mapGraphNodeToJsonDto(geotag.getGraphBinding().getRoad().getEndNode()),
                        geotag.getGraphBinding().getProgress()
                )
        );
    }

    private Geotag mapJsonDtoToGeotag(GeotagJsonDto dto) {
        return new Geotag(
                new GraphBinding(
                        new GraphRoad(
                                mapJsonDtoToGraphNode(dto.getRoad().getStartNode()),
                                mapJsonDtoToGraphNode(dto.getRoad().getEndNode())
                        ),
                        dto.getRoad().getProgress()
                ),
                new GCS(dto.getCoordinates().getLatitude(), dto.getCoordinates().getLongitude())
        );
    }

    public String encodeGeotag(Geotag geotag) {
        try {
            return objectMapper.writeValueAsString(mapGeotagToJsonDto(geotag));
        } catch (Exception e) {
            return null;
        }
    }

    public Geotag decodeGeotag(String json) {
        try {
            return mapJsonDtoToGeotag(objectMapper.readValue(json, GeotagJsonDto.class));
        } catch (Exception e) {
            return null;
        }
    }

    private static class GeotagJsonDto {

        @JsonProperty("coordinates")
        private CoordinatesJsonDto coordinates;
        @JsonProperty("road")
        private RoadJsonDto road;

        public GeotagJsonDto() {

        }

        public GeotagJsonDto(CoordinatesJsonDto coordinates, RoadJsonDto road) {
            this.coordinates = coordinates;
            this.road = road;
        }

        public CoordinatesJsonDto getCoordinates() {
            return coordinates;
        }

        public RoadJsonDto getRoad() {
            return road;
        }
    }

    private static class CoordinatesJsonDto {
        @JsonProperty("latitude")
        private Double latitude;
        @JsonProperty("longitude")
        private Double longitude;

        public CoordinatesJsonDto() {

        }

        public CoordinatesJsonDto(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        @Override public String toString() {
            return "CoordinatesJsonDto{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    private static class NodeJsonDto {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("coordinates")
        private CoordinatesJsonDto coordinates;

        public NodeJsonDto() {

        }

        public NodeJsonDto(Integer id, CoordinatesJsonDto coordinates) {
            this.id = id;
            this.coordinates = coordinates;
        }

        public CoordinatesJsonDto getCoordinates() {
            return coordinates;
        }

        public Integer getId() {
            return id;
        }

        @Override public String toString() {
            return "NodeJsonDto{" +
                    "id='" + id + '\'' +
                    ", coordinates=" + coordinates +
                    '}';
        }
    }

    private static class RoadJsonDto {
        @JsonProperty("startNode")
        private NodeJsonDto startNode;
        @JsonProperty("endNode")
        private NodeJsonDto endNode;
        @JsonProperty("progress")
        private Double progress;

        public RoadJsonDto() {

        }

        public RoadJsonDto(NodeJsonDto startNode, NodeJsonDto endNode, Double progress) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.progress = progress;
        }

        public NodeJsonDto getStartNode() {
            return startNode;
        }

        public NodeJsonDto getEndNode() {
            return endNode;
        }

        public Double getProgress() {
            return progress;
        }

        @Override public String toString() {
            return "RoadJsonDto{" +
                    "startNode=" + startNode +
                    ", endNode=" + endNode +
                    ", progress=" + progress +
                    '}';
        }
    }
}
