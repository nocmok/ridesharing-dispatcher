package com.nocmok.orp.solver.api;

import java.util.List;
import java.util.Optional;

public interface OrpSolver {

    /**
     * Вычисляет список оптимальных тс для обработки запроса.
     * Возвращает отсортированный список от более оптимальных до менее оптимальных тс.
     * В возвращаемом списке содержатся только те тс, которые могут обработать запрос без нарушения ограничений.
     * Поэтому длина списка может быть меньше чем запрошенная или список может вообще быть пустым.
     */
    List<RequestMatching> getTopKCandidateVehicles(Request request, int kCandidates);

    Optional<RequestMatching> getRequestMatchingForVehicle(Request request, String vehicleId);

    /**
     * Возвращает Optional.empty() если метод завершился корректно, но план не поменялся
     */
    Optional<RequestCancellation> cancelRequest(Request request, String vehicleId);
}
