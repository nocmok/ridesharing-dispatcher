package com.nocmok.orp.orp_solver.service.dispatching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

public interface ServiceRequestDispatchingService {

    void dispatchServiceRequest(ServiceRequestDto serviceRequest);

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ServiceRequestDto {

        /**
         * Идентификатор запроса
         */
        private final String requestId;

        /**
         * Идентификатор вершины графа, привязанной к точке посадки
         */
        private final Integer pickupNodeId;

        /**
         * Идентификатор вершины графа, привязанной к точке высадки
         */
        private final int dropoffNodeId;

        /**
         * Время создания запроса
         */
        private final Instant requestedAt;

        /**
         * Ограничение на задержку связанную с применением райдшеринга.
         * Например, если
         * detourConstraint = 1.5
         * t = оценка времени кратчайшего маршрута от точки посадки до точки высадки
         * T = оценка времени построенного маршрута от точки посадки до точки высадки
         * то T <= detourConstraint * t
         */
        private final Double detourConstraint;

        /**
         * Максимальное допустимое время ожидания тс от момента формирования запроса
         */
        private final Integer maxPickupDelaySeconds;

        /**
         * Нагрузка оказываемая на тс при выполнении запроса, например количество человек
         */
        private final Integer load;

    }
}
