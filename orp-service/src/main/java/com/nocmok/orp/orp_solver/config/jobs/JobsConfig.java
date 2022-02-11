package com.nocmok.orp.orp_solver.config.jobs;

import com.nocmok.orp.orp_solver.config.KafkaProducerConfig;
import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.config.storage.StorageConfig;
import com.nocmok.orp.orp_solver.job.ServiceRequestOutboxKafkaExportingJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobsConfig {

    @Autowired
    private PostgressConfig postgressConfig;
    @Autowired
    private StorageConfig storageConfig;
    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Bean
    public ServiceRequestOutboxKafkaExportingJob serviceRequestOutboxKafkaExportingJob() {
        return new ServiceRequestOutboxKafkaExportingJob(
                postgressConfig.transactionTemplate(),
                storageConfig.requestMatchingOutboxStorage(),
                1000,
                kafkaProducerConfig.kafkaTemplate());
    }
}
