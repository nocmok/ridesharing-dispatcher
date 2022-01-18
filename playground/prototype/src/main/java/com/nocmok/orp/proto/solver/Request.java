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
    private int[] departureTimeWindow;
    private int[] arrivalTimeWindow;
    // Нагрузка на тс, оказываемое выполнением запроса
    private int load;
    @Setter
    private State state = State.PENDING;

    public enum State {
        PENDING,
        SERVING,
        SERVED,
        DENIED,
    }
}
