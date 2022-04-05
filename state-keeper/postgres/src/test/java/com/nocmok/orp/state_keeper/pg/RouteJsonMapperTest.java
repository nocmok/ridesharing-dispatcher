package com.nocmok.orp.state_keeper.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteJsonMapperTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDecodeNonEmptyString() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = mapper.decodeRoute("[{\"nodeId\":\"0\",\"lat\":0.0,\"lon\":0.0}]");
        assertEquals(List.of(new GraphNode(0,new GCS(0d,0d))), route);
    }

    @Test
    public void testDecodeEmptyString() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = mapper.decodeRoute("");
        assertEquals(Collections.emptyList(), route);
    }

    @Test
    public void testDecodeEmptyArray() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = mapper.decodeRoute("[]");
        assertEquals(Collections.emptyList(), route);
    }

    @Test
    public void testDecodeEmptyNull() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = mapper.decodeRoute(null);
        assertEquals(Collections.emptyList(), route);
    }

    @Test
    public void testDecodeEmptyNullString() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = mapper.decodeRoute("null");
        assertEquals(Collections.emptyList(), route);
    }

    @Test
    public void testEncodeNonEmptyRoute() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = List.of(new GraphNode(0,new GCS(0d,0d)));
        var json = mapper.encodeRoute(route);
        assertEquals("[{\"nodeId\":\"0\",\"lat\":0.0,\"lon\":0.0}]", json);
    }

    @Test
    public void testEncodeEmptyRoute() {
        var mapper = new RouteJsonMapper(objectMapper);
        var route = Collections.<GraphNode>emptyList();
        var json = mapper.encodeRoute(route);
        assertEquals("[]", json);
    }

    @Test
    public void testEncodeNull() {
        var mapper = new RouteJsonMapper(objectMapper);
        var json = mapper.encodeRoute(null);
        assertEquals("[]", json);
    }
}
