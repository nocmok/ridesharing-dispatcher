package com.nocmok.orp.orp_solver.service.request_execution;

import com.nocmok.orp.postgres.storage.dto.OrderStatus;

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
