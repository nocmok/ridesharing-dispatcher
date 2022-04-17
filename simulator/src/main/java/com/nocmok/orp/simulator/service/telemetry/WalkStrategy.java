package com.nocmok.orp.simulator.service.telemetry;

public interface WalkStrategy {

    Telemetry nextTelemetry(double time);
}
