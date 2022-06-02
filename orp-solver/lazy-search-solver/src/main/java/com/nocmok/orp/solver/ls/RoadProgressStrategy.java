package com.nocmok.orp.solver.ls;

public interface RoadProgressStrategy {

    /**
     * Вычисляет степень прохождения дорожного сегмента транспортным средством
     */
    Double getProgress(Double roadStartNodeLatitude, Double roadStartNodeLongitude,
                       Double roadEndNodeLatitude, Double roadEndNodeLongitude,
                       Double vehicleLatitude, Double vehicleLongitude);
}
