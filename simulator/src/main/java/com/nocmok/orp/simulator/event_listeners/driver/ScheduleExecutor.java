package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.Segment;

public interface ScheduleExecutor {

    void tryExecuteSchedule(double time);
}
