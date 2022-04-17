package com.nocmok.orp.simulator.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({
        KafkaConfig.class
})
@EnableScheduling
public class ApplicationConfig {
}
