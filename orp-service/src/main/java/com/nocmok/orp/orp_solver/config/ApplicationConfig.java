package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.orp_solver.config.jackson.JacksonConfig;
import com.nocmok.orp.orp_solver.config.kafka.KafkaConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.config.graph_index.GraphIndexConfig;
import com.nocmok.orp.orp_solver.config.schedule.ScheduleConfig;
import com.nocmok.orp.orp_solver.config.state_keeper.StateKeeperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        StateKeeperConfig.class,
        GraphIndexConfig.class,
        JacksonConfig.class,
        PostgressConfig.class,
        ScheduleConfig.class,
})
public class ApplicationConfig {
}
