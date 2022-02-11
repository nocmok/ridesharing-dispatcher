package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.orp_solver.config.jobs.JobsConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.config.schedule.ScheduleConfig;
import com.nocmok.orp.orp_solver.config.service.ServiceConfig;
import com.nocmok.orp.orp_solver.config.storage.StorageConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KafkaConfig.class,
        StateKeeperConfig.class,
        RoadIndexConfig.class,
        JacksonConfig.class,
        PostgressConfig.class,
        ServiceConfig.class,
        StorageConfig.class,
        ScheduleConfig.class,
        JobsConfig.class
})
public class ApplicationConfig {
}
