package com.nocmok.orp.api.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import com.nocmok.orp.postgres.PostgresConfig;
import com.nocmok.orp.state_keeper.postgres.StateKeeperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        PostgresConfig.class,
        StateKeeperConfig.class,
})
public class ApplicationConfig {
}
