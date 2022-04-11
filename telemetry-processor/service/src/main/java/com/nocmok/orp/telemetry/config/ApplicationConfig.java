package com.nocmok.orp.telemetry.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class
})
public class ApplicationConfig {
}
