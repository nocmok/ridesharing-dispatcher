package com.nocmok.orp.graph.mem_stub.client;

import com.nocmok.orp.graph.api.ObjectUpdater;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphObjectsStorageImplTest {

    @Test
    public void testUpdateObject() {
        var objectsStorage = new GraphObjectStorageImpl("localhost", 8083);
//        assertTrue(objectsStorage.getObject("0").isEmpty());
        objectsStorage.updateObject(new ObjectUpdater("0", "0", "1", -7.4145588E7, 4.0768E7));
        var obj = objectsStorage.getObject("0");
        assertTrue(obj.isPresent());
        assertEquals(obj.get().getId(), "0");
        assertEquals(obj.get().getSegment().getStartNode().getId(), "0");
        assertEquals(obj.get().getSegment().getEndNode().getId(), "1");
        assertEquals(obj.get().getLatitude(), -7.4145588E7);
        assertEquals(obj.get().getLongitude(), 4.0768E7);
    }
}
