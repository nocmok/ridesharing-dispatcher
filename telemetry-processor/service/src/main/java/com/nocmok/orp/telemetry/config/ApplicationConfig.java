package com.nocmok.orp.telemetry.config;

import com.nocmok.orp.telemetry.config.graph_index.GraphIndexConfig;
import com.nocmok.orp.telemetry.config.jackson.JacksonConfig;
import com.nocmok.orp.telemetry.config.kafka.KafkaConfig;
import com.nocmok.orp.telemetry.config.state_keeper.StateKeeperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        StateKeeperConfig.class,
        GraphIndexConfig.class,
        JacksonConfig.class
})
public class ApplicationConfig {
}
