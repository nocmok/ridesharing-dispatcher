package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.orp_solver.kafka.orp_input.dto.MatchVehiclesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrpInputRequestHandlers {

    private static final Logger log = LoggerFactory.getLogger(OrpInputRequestHandlers.class);

    private OrpSolver solver;

    public OrpInputRequestHandlers(OrpSolver solver) {
        this.solver = solver;
    }

    public void handleMatchVehicleRequest(MatchVehiclesRequest request) {
        var topKVehicles = solver.getTopKCandidateVehicles(
                new Request(
                        request.getRequestId(),
                        request.getPickupNodeId(),
                        request.getPickupLat(),
                        request.getPickupLon(),
                        request.getDropoffNodeId(),
                        request.getDropoffLat(),
                        request.getDropoffLon(),
                        request.getRequestedAt(),
                        request.getDetourConstraint(),
                        request.getMaxPickupDelaySeconds(),
                        request.getLoad()
                ),
                request.getTopK());
        log.info("matched vehicles\n" + topKVehicles);
    }
}
