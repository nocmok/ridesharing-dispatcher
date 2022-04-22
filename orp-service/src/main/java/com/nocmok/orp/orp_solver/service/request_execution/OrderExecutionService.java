package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.state_keeper.api.VehicleState;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для обработки прогресса выполнения запросов
 */
public interface OrderExecutionService {

    /**
     * Обновляет статус выполнения запроса.
     *
     * @param sessionId идентификатор сессии для которой обновляется статус выполнения запроса.
     */
    void updateOrderStatus(String sessionId, String orderId, OrderStatus updatedStatus);
}
