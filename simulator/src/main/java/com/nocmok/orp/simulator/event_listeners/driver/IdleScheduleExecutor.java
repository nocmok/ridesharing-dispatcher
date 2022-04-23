package com.nocmok.orp.simulator.event_listeners.driver;

public class IdleScheduleExecutor implements ScheduleExecutor {

    private static final IdleScheduleExecutor INSTANCE = new IdleScheduleExecutor();

    public static IdleScheduleExecutor instance() {
        return INSTANCE;
    }

    @Override public void tryExecuteSchedule(double time) {

    }
}
