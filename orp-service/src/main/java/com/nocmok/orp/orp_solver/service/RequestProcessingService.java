package com.nocmok.orp.orp_solver.service;

import com.nocmok.orp.core_api.OrpSolver;
import com.nocmok.orp.core_api.Request;
import lombok.extern.slf4j.Slf4j;

/**
 * Класс-стратегия для обработки запроса на матчинг
 */
@Slf4j
public class RequestProcessingService {

    private OrpSolver solver;
    private TaskAssigner taskAssigner;
    private Integer candidatesToFetch;

    public RequestProcessingService(OrpSolver solver, TaskAssigner taskAssigner, Integer candidatesToFetch) {
        this.solver = solver;
        this.taskAssigner = taskAssigner;
        this.candidatesToFetch = candidatesToFetch;
    }

    public void processRequest(Request request) {
        var candidates = solver.getTopKCandidateVehicles(request, candidatesToFetch);
        if (candidates.isEmpty()) {
            log.debug("no candidates to serve request\n" + request);
            return;
        }
        var servingVehicleId = taskAssigner.assign(candidates);
        if (servingVehicleId.isEmpty()) {
            log.debug("all candidates to serve request reserved for another request\n");
            initiateRetry(request);
            return;
        }
    }

    private void initiateRetry(Request request) {
        log.debug("request retry initiated, but not implemented yet ...");
    }
}
