package com.nocmok.orp.proto.benchmark;

import com.nocmok.orp.proto.simulator.Metrics;
import com.nocmok.orp.proto.simulator.Simulator;
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

    public Metrics runBenchmarking() {
        int nextRequest = 0;
        for (int i = 0; i < nIterations; ++i) {
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
