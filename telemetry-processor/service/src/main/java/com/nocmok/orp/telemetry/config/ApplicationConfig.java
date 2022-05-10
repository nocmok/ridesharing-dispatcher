package com.nocmok.orp.telemetry.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import com.nocmok.orp.postgres.PostgresConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        PostgresConfig.class,
})
public class ApplicationConfig {
}
