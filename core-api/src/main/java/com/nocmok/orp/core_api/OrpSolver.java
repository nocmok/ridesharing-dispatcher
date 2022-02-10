package com.nocmok.orp.core_api;

import java.util.List;

public interface OrpSolver {

    /**
     * Вычисляет список оптимальных тс для обработки запроса.
     * Возвращает отсортированный список от более оптимальных до менее оптимальных тс.
     * В возвращаемом списке содержатся только те тс, которые могут обработать запрос без нарушения ограничений.
     * Поэтому длина списка может быть меньше чем запрошенная или список может вообще быть пустым.
     */
    List<RequestMatching> getTopKCandidateVehicles(Request request, int kCandidates);

    /**
     * Добавляет запрос в план тс.
     */
    void acceptRequest(Vehicle vehicle, Request request);

    /**
     * Удаляет запрос из плана тс.
     */
    void cancelRequest(Vehicle vehicle, Request request);
}
