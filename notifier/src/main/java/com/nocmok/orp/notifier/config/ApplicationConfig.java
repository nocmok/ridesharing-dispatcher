package com.nocmok.orp.notifier.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// Для добавления конфига из внешнего модуля.
@Import({
        KafkaConfig.class
})
@Configuration
public class ApplicationConfig {
}
