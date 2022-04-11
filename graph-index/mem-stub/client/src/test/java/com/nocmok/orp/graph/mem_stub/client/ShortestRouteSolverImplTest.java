package com.nocmok.orp.graph.mem_stub.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ShortestRouteSolverImplTest {

    @Test
    public void testGetShortestRoute() {
        var shortestRouteSolver = new ShortestRouteSolverImpl("localhost", 8083);
        var route = shortestRouteSolver.getShortestRoute("10", "100");
        assertFalse(route.isEmpty());
    }
}
