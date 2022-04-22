package com.nocmok.orp.orp_solver.service.request_execution;

public class IllegalRequestExecution extends RuntimeException {

    public IllegalRequestExecution() {
    }

    public IllegalRequestExecution(String message) {
        super(message);
    }

    public IllegalRequestExecution(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRequestExecution(Throwable cause) {
        super(cause);
    }

    public IllegalRequestExecution(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
