package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.orp_solver.config.listener.ListenerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        VehicleStateServiceConfig.class,
        RoadIndexConfig.class,
        ListenerConfig.class,
        JacksonConfig.class
})
public class ApplicationConfig {
}
