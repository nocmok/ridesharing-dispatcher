package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class RouteJsonMapper {

    private ObjectMapper objectMapper;

    public RouteJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private GraphNode mapJsonDtoToGraphNode(GraphNodeJsonDto dto) {
        return new GraphNode(Integer.parseInt(dto.getNodeId()), new GCS(dto.getLat(), dto.getLon()));
    }

    private GraphNodeJsonDto mapGraphNodeToJsonDto(GraphNode graphNode) {
        return new GraphNodeJsonDto(Integer.toString(graphNode.getNodeId()), graphNode.getCoordinates().lat(), graphNode.getCoordinates().lon());
    }

    public String encodeRoute(List<GraphNode> route) {
        try {
            return objectMapper.writeValueAsString(route.stream()
                    .map(this::mapGraphNodeToJsonDto)
                    .collect(Collectors.toUnmodifiableList()));
        } catch (Exception e) {
            return "[]";
        }
    }

    public List<GraphNode> decodeRoute(String json) {
        try {
            GraphNodeJsonDto[] route = objectMapper.readValue(json, GraphNodeJsonDto[].class);
            return Arrays.stream(route)
                    .map(this::mapJsonDtoToGraphNode)
                    .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static class GraphNodeJsonDto {
        @JsonProperty("nodeId")
        private String nodeId;
        @JsonProperty("lat")
        private Double lat;
        @JsonProperty("lon")
        private Double lon;

        public GraphNodeJsonDto() {

        }

        public GraphNodeJsonDto(String nodeId, Double lat, Double lon) {
            this.nodeId = nodeId;
            this.lat = lat;
            this.lon = lon;
        }

        public String getNodeId() {
            return nodeId;
        }

        public Double getLat() {
            return lat;
        }

        public Double getLon() {
            return lon;
        }
    }
}
