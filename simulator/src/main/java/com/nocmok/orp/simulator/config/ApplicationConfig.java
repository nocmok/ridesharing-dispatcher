package com.nocmok.orp.simulator.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import com.nocmok.orp.postgres.PostgresConfig;
import com.nocmok.orp.state_keeper.postgres.StateKeeperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({
        KafkaConfig.class,
        PostgresConfig.class,
        StateKeeperConfig.class
})
@EnableScheduling
@EnableKafka
public class ApplicationConfig {
}
