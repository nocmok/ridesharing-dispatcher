package com.nocmok.orp.simulator.config.simulator;

import com.nocmok.orp.graph.api.SpatialGraphMetadataStorage;
import com.nocmok.orp.graph.api.SpatialGraphObjectsStorage;
import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_listeners.driver.VirtualDriver;
import com.nocmok.orp.simulator.service.api.DriverApi;
import com.nocmok.orp.simulator.service.telemetry.TelemetrySender;
import com.nocmok.orp.simulator.storage.VehicleSessionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class SimulatorConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean("virtualDriverFactory")
    @Autowired
    public Function<String, VirtualDriver> virtualDriverFactory(EventBus eventBus, TelemetrySender telemetrySender,
                                                                SpatialGraphObjectsStorage graphObjectsStorage,
                                                                SpatialGraphMetadataStorage graphMetadataStorage,
                                                                DriverApi driverApi, VehicleSessionStorage vehicleSessionStorage) {
        return (sessionId) -> new VirtualDriver(sessionId, eventBus, telemetrySender, graphMetadataStorage, graphObjectsStorage, driverApi,
                vehicleSessionStorage);
    }
}
