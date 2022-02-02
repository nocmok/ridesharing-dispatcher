package com.nocmok.orp.orp_solver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class
})
public class ApplicationConfig {
}
