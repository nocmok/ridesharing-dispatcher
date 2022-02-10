package com.nocmok.orp.orp_solver.config.storage;

import com.nocmok.orp.orp_solver.config.postgres.PostgressConfig;
import com.nocmok.orp.orp_solver.storage.RequestMatchingOutboxStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Autowired
    private PostgressConfig postgressConfig;

    @Bean
    public RequestMatchingOutboxStorage requestMatchingOutboxStorage() {
        return new RequestMatchingOutboxStorage(postgressConfig.namedParameterJdbcTemplate());
    }
}
