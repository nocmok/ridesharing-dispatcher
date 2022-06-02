package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.kafka.config.KafkaConfig;
import com.nocmok.orp.graph_index.postgres.GraphIndexConfig;
import com.nocmok.orp.orp_solver.config.jackson.JacksonConfig;
import com.nocmok.orp.orp_solver.config.schedule.ScheduleConfig;
import com.nocmok.orp.postgres.PostgresConfig;
import com.nocmok.orp.state_keeper.postgres.StateKeeperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        PostgresConfig.class,
        GraphIndexConfig.class,
        JacksonConfig.class,
        ScheduleConfig.class,
        StateKeeperConfig.class,
})
public class ApplicationConfig {
}
