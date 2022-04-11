package com.nocmok.orp.graph.mem_stub.client;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphMetadataStorageImplTest {

    @Test
    public void testGetSegment() {
        var graphMetadataStorage = new GraphMetadataStorageImpl("localhost", 8083);
        var segment = graphMetadataStorage.getSegment("0", "1");
        assertNotNull(segment);
        assertEquals(segment.getStartNode().getId(), "0");
        assertEquals(segment.getEndNode().getId(), "1");
    }

    @Test
    public void testGetSegments() {
        var graphMetadataStorage = new GraphMetadataStorageImpl("localhost", 8083);
        var segment = graphMetadataStorage.getSegments(List.of("0", "0"), List.of("1", "2"));
        assertNotNull(segment);
        assertEquals(segment.size(), 2);
    }
}
