package com.nocmok.orp.orp_solver.kafka.orp_input;

import com.nocmok.orp.orp_solver.kafka.orp_input.dto.MatchVehiclesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrpInputRequestHandlers {

    private static final Logger log = LoggerFactory.getLogger(OrpInputRequestHandlers.class);

    public void handleMatchVehicleRequest(MatchVehiclesRequest request) {
        log.info("handled\n" + request.toString());
    }
}
