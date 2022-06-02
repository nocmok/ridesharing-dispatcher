package com.nocmok.orp.proto.solver;

import com.nocmok.orp.proto.pojo.GPS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class Request {

    private int requestId;
    private int userId;
    private GPS departurePoint;
    private GPS arrivalPoint;
    private int departureNode;
    private int arrivalNode;
    private int[] departureTimeWindow;
    private int[] arrivalTimeWindow;
    // Нагрузка на тс, оказываемое выполнением запроса
    private int load;
    @Setter
    @Builder.Default
    private State state = State.PENDING;

    public int getEarliestDepartureTime() {
        return departureTimeWindow[0];
    }

    public int getLatestDepartureTime() {
        return departureTimeWindow[1];
    }

    public int getEarliestArrivalTime() {
        return arrivalTimeWindow[0];
    }

    public int getLatestArrivalTime() {
        return arrivalTimeWindow[1];
    }

    public enum State {
        PENDING,
        SERVING,
        SERVED,
        DENIED,
    }
}
