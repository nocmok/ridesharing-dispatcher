package com.nocmok.orp.orp_solver.kafka.orp_input;

public abstract class RequestHandler<T> {
    @SuppressWarnings("unchecked") final void _handle(Object requestDto) {
        handle((T) requestDto);
    }

    public abstract void handle(T requestDto);
}
