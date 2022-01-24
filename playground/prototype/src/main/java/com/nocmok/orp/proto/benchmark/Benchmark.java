package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.simulator.Simulator;
import com.nocmok.orp.proto.solver.Matching;
import com.nocmok.orp.proto.solver.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// Принимает на вход симулятор со всеми его гиперпараметрами
// Необходимое количество циклов
// Запускает n циклов симуляции и считает метрики
// Метрики:
// 1) Суммарное пройденное всеми тс расстояние
// 2) Среднее время ожидания клиентом тс
// 3) Количество отклоненных запросов
@AllArgsConstructor
@Builder
public class Benchmark {
    private Simulator simulator;
    private int nIterations;
    private List<DelayedRequest> requestPlan;

    private double distance(GPS from, GPS to) {
        return Math.hypot(to.x - from.x, to.y - from.y);
    }

    private void updateTotalDistances(Metrics metrics) {
//        for (var vehicle : simulator.getState().getVehicleList()) {
//            int nGps = vehicle.getGpsLog().size();
//            if (nGps < 2) {
//                continue;
//            }
//            metrics.totalDistance += distance(vehicle.getGpsLog().get(nGps - 2), vehicle.getGpsLog().get(nGps - 1));
//        }
    }

    private void updateTotalRequests(Metrics metrics) {
        ++metrics.totalRequests;
    }

    private void updateDeniedRequests(Metrics metrics) {
        ++metrics.deniedRequests;
    }

    public Metrics runBenchmarking() {
        Metrics metrics = new Metrics();

        int nextRequest = 0;
        for (int i = 0; i < nIterations; ++i) {
            while (nextRequest < requestPlan.size() && requestPlan.get(nextRequest).getTimeToAccept() <= simulator.getState().getTime()) {
                var request = requestPlan.get(nextRequest).getRequest();
                var matching = simulator.acceptRequest(request);

                updateTotalDistances(metrics);
                updateTotalRequests(metrics);
                if (matching.getDenialReason() != Matching.DenialReason.ACCEPTED) {
                    updateDeniedRequests(metrics);
                }

                ++nextRequest;
            }
            simulator.ticTac(1);
        }

        return metrics;
    }

    @Getter
    public static class Metrics {
        private double totalDistance;
        private int totalRequests;
        private int deniedRequests;

        @Override public String toString() {
            return "totalDistance=" + totalDistance +
                    "\ntotalRequests=" + totalRequests +
                    "\ndeniedRequests=" + deniedRequests;
        }
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
