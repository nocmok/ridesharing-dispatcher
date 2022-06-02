package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.simulator.Metrics;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
public class Benchmark {

    @Getter
    private Simulator simulator;
    private List<DelayedRequest> requestPlan;

    public Metrics runBenchmarking() {
        int nextRequest = 0;
        while (simulator.getProcessedRequests() < requestPlan.size()) {
            while (nextRequest < requestPlan.size() && requestPlan.get(nextRequest).getTimeToAccept() <= simulator.getState().getTime()) {
                var request = requestPlan.get(nextRequest).getRequest();
                simulator.acceptRequest(request);
                ++nextRequest;
            }
            simulator.ticTac(1);
        }

        return simulator.getMetrics();
    }

    @Getter
    public static class DelayedRequest {
        private int timeToAccept;
        private Request request;

        public DelayedRequest(Request request, int timeToAccept) {
            this.request = request;
            this.timeToAccept = timeToAccept;
        }
    }
}
